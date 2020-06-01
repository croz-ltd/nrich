package net.croz.nrich.registry.core.model;

import lombok.Getter;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public final class ManagedTypeWrapper {

    private final IdentifiableType<?> identifiableType;

    public ManagedTypeWrapper(final ManagedType<?> managedType) {
        Assert.isTrue(managedType instanceof IdentifiableType, "Managed type has no id attribute, no operations will be possible!");

        identifiableType = (IdentifiableType<?>) managedType;
    }

    public boolean isVersioned() {
        return identifiableType.hasVersionAttribute();
    }

    public String idAttributeName() {
        return identifiableType.getId(identifiableType.getIdType().getJavaType()).getName();
    }

    public boolean isIdentifierAssigned() {
        boolean isIdentifierAssigned = false;

        if (identifiableType.hasSingleIdAttribute()) {
            isIdentifierAssigned = Arrays.stream(identifiableType.getClass().getDeclaredAnnotations()).noneMatch(annotation -> GeneratedValue.class.equals(annotation.annotationType()));
        }

        return isIdentifierAssigned;
    }

    public boolean isCompositeIdentity() {
        return !identifiableType.hasSingleIdAttribute();
    }

    public List<String> compositeIdentityPropertyNameList() {
        return identifiableType.getIdClassAttributes().stream()
                .map(Attribute::getName)
                .collect(Collectors.toList());
    }
}
