package net.croz.nrich.search.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.Assert;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Arrays;

@AllArgsConstructor
public class JpaEntityAttributeResolver {

    private final ManagedType<?> managedType;

    public AttributeHolder resolveAttributeByPath(final String path) {
        Assert.notNull(path, "Path must be defined when searching for attribute!");

        final String[] pathList = PathResolvingUtil.convertToPathList(path);

        Attribute<?, ?> attribute = resolveAttributeByName(managedType, pathList[0]);
        boolean isPlural = attribute instanceof PluralAttribute;
        ManagedType<?> managedType = resolveManagedTypeFromAttribute(attribute);

        if (managedType != null && pathList.length > 1) {
            final String[] restOfPathList = Arrays.copyOfRange(pathList, 1, pathList.length);
            for (final String currentPath : restOfPathList) {
                if (managedType == null) {
                    attribute = null;
                    isPlural = false;
                    break;
                }
                attribute = resolveAttributeByName(managedType, currentPath);
                managedType = resolveManagedTypeFromAttribute(attribute);
                isPlural |= attribute instanceof PluralAttribute;
            }
        }

        return new AttributeHolder(attribute, managedType, isPlural);
    }

    private ManagedType<?> resolveManagedTypeFromAttribute(final Attribute<?, ?> attribute) {
        ManagedType<?> managedType = null;

        if (attribute instanceof SingularAttribute && ((SingularAttribute<?, ?>) attribute).getType() instanceof ManagedType) {
            managedType = ((ManagedType<?>) ((SingularAttribute<?, ?>) attribute).getType());
        }
        else if (attribute instanceof PluralAttribute && ((PluralAttribute<?, ?, ?>) attribute).getElementType() instanceof ManagedType) {
            managedType = ((ManagedType<?>) ((PluralAttribute<?, ?, ?>) attribute).getElementType());
        }

        return managedType;
    }

    private Attribute<?, ?> resolveAttributeByName(final ManagedType<?> managedType, final String attributeName) {
        return managedType.getAttributes().stream().filter(attribute -> attribute.getName().equals(attributeName)).findFirst().orElse(null);
    }

    @Data
    public static class AttributeHolder {

        private final Attribute<?, ?> attribute;

        private final ManagedType<?> managedType;

        private final boolean isPlural;

    }
}
