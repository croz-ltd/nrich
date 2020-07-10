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

    private final boolean isCompositeIdentity;

    private final List<String> compositeIdentityPropertyNameList;

    private final Map<String, Class<?>> compositeIdentityNameTypeMap;

    private final String idAttributeName;

    private final boolean isIdentifierAssigned;

    private final List<SingularAttribute<?, ?>> associationList;

    private final EmbeddableType<?> embeddableIdType;

    public ManagedTypeWrapper(final ManagedType<?> managedType) {
        Assert.isTrue(managedType instanceof IdentifiableType, "Managed type has no id attribute, no operations will be possible!");

        identifiableType = (IdentifiableType<?>) managedType;
        embeddableIdType = ((IdentifiableType<?>) managedType).getIdType() instanceof EmbeddableType ? (EmbeddableType<?>) ((IdentifiableType<?>) managedType).getIdType() : null;
        isCompositeIdentity = !identifiableType.hasSingleIdAttribute() || embeddableIdType != null;
        idAttributeName = identifiableType.hasSingleIdAttribute() ? identifiableType.getId(identifiableType.getIdType().getJavaType()).getName() : null;
        compositeIdentityNameTypeMap = resolveCompositeIdentityNameTypeMap();
        compositeIdentityPropertyNameList = new ArrayList<>(compositeIdentityNameTypeMap.keySet());
        isIdentifierAssigned = resolveIsIdentifierAssigned();
        associationList = managedType.getSingularAttributes().stream()
                .filter(Attribute::isAssociation).collect(Collectors.toList());
    }

    private Map<String, Class<?>> resolveCompositeIdentityNameTypeMap() {
        Map<String, Class<?>> propertyNameList = Collections.emptyMap();

        if (!identifiableType.hasSingleIdAttribute()) {
            propertyNameList = identifiableType.getIdClassAttributes().stream()
                    .collect(Collectors.toMap(Attribute::getName, Attribute::getJavaType));
        }
        else if (embeddableIdType != null) {
            propertyNameList = Collections.singletonMap(idAttributeName, identifiableType.getIdType().getJavaType());
        }

        return propertyNameList;
    }

    private boolean resolveIsIdentifierAssigned() {
        return identifiableType.getAttributes().stream()
                .map(Attribute::getJavaMember)
                .filter(member -> member instanceof Field)
                .map(member -> (Field) member)
                .map(Field::getDeclaredAnnotations)
                .noneMatch(annotationList -> Arrays.stream(annotationList).anyMatch(annotation -> GeneratedValue.class.equals(annotation.annotationType())));
    }
}
