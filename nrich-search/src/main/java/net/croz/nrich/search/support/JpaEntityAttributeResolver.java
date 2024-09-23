/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.search.support;

import net.croz.nrich.search.model.AttributeHolder;
import net.croz.nrich.search.util.AttributeResolvingUtil;
import net.croz.nrich.search.util.PathResolvingUtil;
import org.springframework.util.Assert;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.Arrays;

public record JpaEntityAttributeResolver(ManagedType<?> managedType) {

    public AttributeHolder resolveAttributeByPath(String path) {
        Assert.notNull(path, "Path must be defined when searching for attribute!");

        String[] pathList = PathResolvingUtil.convertToPathList(path);

        Attribute<?, ?> attribute = AttributeResolvingUtil.resolveAttributeByName(managedType, pathList[0]);
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
                attribute = AttributeResolvingUtil.resolveAttributeByName(currentManagedType, currentPath);
                currentManagedType = resolveManagedTypeFromAttribute(attribute);
                isPlural |= attribute instanceof PluralAttribute;
            }
        }

        return new AttributeHolder(attribute, currentManagedType, isPlural);
    }

    private ManagedType<?> resolveManagedTypeFromAttribute(Attribute<?, ?> attribute) {
        ManagedType<?> currentManagedType = null;

        if (attribute instanceof SingularAttribute && ((SingularAttribute<?, ?>) attribute).getType() instanceof ManagedType<?> attributeManagedType) {
            currentManagedType = attributeManagedType;
        }
        else if (attribute instanceof PluralAttribute && ((PluralAttribute<?, ?, ?>) attribute).getElementType() instanceof ManagedType<?> attributeManagedType) {
            currentManagedType = attributeManagedType;
        }

        return currentManagedType;
    }
}
