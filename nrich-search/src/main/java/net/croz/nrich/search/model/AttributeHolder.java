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

package net.croz.nrich.search.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;

@RequiredArgsConstructor
@Getter
public class AttributeHolder {

    private final Attribute<?, ?> attribute;

    private final ManagedType<?> managedType;

    private final boolean isPlural;

    public boolean isFound() {
        return attribute != null;
    }

    public boolean isElementCollection() {
        return isFound() && attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
    }

    public static AttributeHolder notFound() {
        return new AttributeHolder(null, null, false);
    }
}
