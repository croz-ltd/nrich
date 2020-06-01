package net.croz.nrich.registry.core.model;

import lombok.Getter;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
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

    public ManagedTypeWrapper(final ManagedType<?> managedType) {
        Assert.isTrue(managedType instanceof IdentifiableType, "Managed type has no id attribute, no operations will be possible!");

        identifiableType = (IdentifiableType<?>) managedType;
        isCompositeIdentity = !identifiableType.hasSingleIdAttribute() || identifiableType.getIdType() instanceof EmbeddableType;
        compositeIdentityNameTypeMap = resolveCompositeIdentityNameTypeMap();
        compositeIdentityPropertyNameList = new ArrayList<>(compositeIdentityNameTypeMap.keySet());
        idAttributeName = identifiableType.hasSingleIdAttribute() ? identifiableType.getId(identifiableType.getIdType().getJavaType()).getName() : null;
        isIdentifierAssigned = resolveIsIdentifierAssigned();
    }

    private Map<String, Class<?>> resolveCompositeIdentityNameTypeMap() {
        Map<String, Class<?>> propertyNameList = Collections.emptyMap();

        if (!identifiableType.hasSingleIdAttribute()) {
            propertyNameList = identifiableType.getIdClassAttributes().stream()
                    .collect(Collectors.toMap(Attribute::getName, Attribute::getJavaType));
        }
        else if (identifiableType.getIdType() instanceof EmbeddableType) {
            propertyNameList = ((EmbeddableType<?>) identifiableType.getIdType()).getAttributes().stream()
                    .collect(Collectors.toMap(Attribute::getName, Attribute::getJavaType));
        }

        return propertyNameList;
    }

    private boolean resolveIsIdentifierAssigned() {
        boolean isIdentifierAssigned = false;

        if (identifiableType.hasSingleIdAttribute()) {
            isIdentifierAssigned = Arrays.stream(identifiableType.getClass().getDeclaredAnnotations()).noneMatch(annotation -> GeneratedValue.class.equals(annotation.annotationType()));
        }

        return isIdentifierAssigned;
    }
}
