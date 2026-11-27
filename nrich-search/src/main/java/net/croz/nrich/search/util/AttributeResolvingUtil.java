package net.croz.nrich.search.util;

import org.hibernate.metamodel.model.domain.internal.AbstractManagedType;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;

public final class AttributeResolvingUtil {

    private AttributeResolvingUtil() {
    }

    public static Attribute<?, ?> resolveAttributeByName(ManagedType<?> managedType, String attributeName) {
        if (managedType instanceof AbstractManagedType<?> abstractManagedType) {
            Attribute<?, ?> attribute = abstractManagedType.findAttribute(attributeName);
            if (attribute == null) {
                return abstractManagedType.findSubTypesAttribute(attributeName);
            }

            return attribute;
        }
        else {
            try {
                return managedType.getAttribute(attributeName);
            }
            catch (Exception ignored) {
                return null;
            }
        }
    }
}
