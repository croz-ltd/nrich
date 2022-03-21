package net.croz.nrich.search.support;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.model.AttributeHolder;
import net.croz.nrich.search.util.PathResolvingUtil;
import org.springframework.util.Assert;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public class JpaEntityAttributeResolver {

    private final ManagedType<?> managedType;

    public AttributeHolder resolveAttributeByPath(String path) {
        Assert.notNull(path, "Path must be defined when searching for attribute!");

        String[] pathList = PathResolvingUtil.convertToPathList(path);

        Attribute<?, ?> attribute = resolveAttributeByName(managedType, pathList[0]);
        boolean isPlural = attribute instanceof PluralAttribute;
        ManagedType<?> currentManagedType = resolveManagedTypeFromAttribute(attribute);

        if (currentManagedType != null && pathList.length > 1) {
            String[] restOfPathList = Arrays.copyOfRange(pathList, 1, pathList.length);
            for (String currentPath : restOfPathList) {
                if (currentManagedType == null) {
                    attribute = null;
                    isPlural = false;
                    break;
                }
                attribute = resolveAttributeByName(currentManagedType, currentPath);
                currentManagedType = resolveManagedTypeFromAttribute(attribute);
                isPlural |= attribute instanceof PluralAttribute;
            }
        }

        return new AttributeHolder(attribute, currentManagedType, isPlural);
    }

    private ManagedType<?> resolveManagedTypeFromAttribute(Attribute<?, ?> attribute) {
        ManagedType<?> currentManagedType = null;

        if (attribute instanceof SingularAttribute && ((SingularAttribute<?, ?>) attribute).getType() instanceof ManagedType) {
            currentManagedType = ((ManagedType<?>) ((SingularAttribute<?, ?>) attribute).getType());
        }
        else if (attribute instanceof PluralAttribute && ((PluralAttribute<?, ?, ?>) attribute).getElementType() instanceof ManagedType) {
            currentManagedType = ((ManagedType<?>) ((PluralAttribute<?, ?, ?>) attribute).getElementType());
        }

        return currentManagedType;
    }

    private Attribute<?, ?> resolveAttributeByName(ManagedType<?> managedType, String attributeName) {
        return managedType.getAttributes().stream()
            .filter(attribute -> attribute.getName().equals(attributeName))
            .findFirst()
            .orElse(null);
    }
}
