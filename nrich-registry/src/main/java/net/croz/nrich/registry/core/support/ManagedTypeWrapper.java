package net.croz.nrich.registry.core.support;

import lombok.Getter;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public final class ManagedTypeWrapper {

    private final IdentifiableType<?> identifiableType;

    private final boolean isIdClassIdentifier;

    private final boolean isEmbeddedIdentifier;

    private final Map<String, Class<?>> idClassPropertyMap;

    private final List<String> idClassPropertyNameList;

    private final String idAttributeName;

    private final boolean isIdentifierAssigned;

    private final List<SingularAttribute<?, ?>> singularAssociationList;

    private final List<SingularAttribute<?, ?>> singularEmbeddedTypeAssociationList;

    private final EmbeddableType<?> embeddableIdType;

    public ManagedTypeWrapper(final ManagedType<?> managedType) {
        Assert.isTrue(managedType instanceof IdentifiableType, "Managed type has no id attribute, no operations will be possible!");

        identifiableType = (IdentifiableType<?>) managedType;
        embeddableIdType = resolveEmbeddedIdentifierType(identifiableType);
        isEmbeddedIdentifier = embeddableIdType != null;
        isIdClassIdentifier = !identifiableType.hasSingleIdAttribute();
        idAttributeName = resolveIdAttributeName(identifiableType);
        idClassPropertyMap = resolveIdClassPropertyMap(identifiableType);
        idClassPropertyNameList = new ArrayList<>(idClassPropertyMap.keySet());
        isIdentifierAssigned = resolveIsIdentifierAssigned(identifiableType);
        singularAssociationList = resolveSingularAssociationList(identifiableType);
        singularEmbeddedTypeAssociationList = isEmbeddedIdentifier ? resolveSingularAssociationList(embeddableIdType) : Collections.emptyList();
    }

    public Class<?> getJavaType() {
        return identifiableType.getJavaType();
    }

    private EmbeddableType<?> resolveEmbeddedIdentifierType(final IdentifiableType<?> identifiableType) {
        return identifiableType.getIdType() instanceof EmbeddableType ? (EmbeddableType<?>) identifiableType.getIdType() : null;
    }

    private String resolveIdAttributeName(final IdentifiableType<?> identifiableType) {
        return identifiableType.hasSingleIdAttribute() ? identifiableType.getId(identifiableType.getIdType().getJavaType()).getName() : null;
    }

    private Map<String, Class<?>> resolveIdClassPropertyMap(final IdentifiableType<?> identifiableType) {
        return identifiableType.hasSingleIdAttribute() ? Collections.emptyMap() : identifiableType.getIdClassAttributes().stream()
                .collect(Collectors.toMap(Attribute::getName, Attribute::getJavaType));
    }

    private boolean resolveIsIdentifierAssigned(final IdentifiableType<?> managedType) {
        return managedType.getAttributes().stream()
                .map(Attribute::getJavaMember)
                .filter(member -> member instanceof Field)
                .map(member -> (Field) member)
                .map(Field::getDeclaredAnnotations)
                .noneMatch(annotationList -> Arrays.stream(annotationList).anyMatch(annotation -> GeneratedValue.class.equals(annotation.annotationType())));
    }

    private List<SingularAttribute<?, ?>> resolveSingularAssociationList(final ManagedType<?> managedType) {
        return managedType.getSingularAttributes().stream()
                .filter(Attribute::isAssociation).collect(Collectors.toList());
    }
}
