package net.croz.nrich.registry.core.model;

import lombok.Getter;
import org.springframework.util.Assert;

import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;

@Getter
public final class ManagedTypeWrapper {

    private final IdentifiableType<?> identifiableType;

    public ManagedTypeWrapper(final ManagedType<?> managedType) {
        Assert.isTrue(managedType instanceof IdentifiableType, "Managed type has no id attribute no operations will be possible!");

        identifiableType = (IdentifiableType<?>) managedType;
    }

    public boolean isVersioned() {
        return identifiableType.hasVersionAttribute();
    }

    public String idAttributeName() {
        return identifiableType.getId(identifiableType.getIdType().getJavaType()).getName();
    }
}
