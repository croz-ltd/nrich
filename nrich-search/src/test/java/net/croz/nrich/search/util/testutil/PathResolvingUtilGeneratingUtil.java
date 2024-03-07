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

package net.croz.nrich.search.util.testutil;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import java.util.Set;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public final class PathResolvingUtilGeneratingUtil {

    private PathResolvingUtilGeneratingUtil() {
    }

    public static Root<?> createRootWithCollectionAttribute(String firstPath, String secondPath, Path<?> result, JoinType joinType) {
        Attribute<?, ?> attribute = mock(Attribute.class);
        Root<?> root = createRootWithAttribute(firstPath, attribute);

        doReturn(true).when(attribute).isCollection();

        Join<?, ?> second = mock(Join.class);
        doReturn(second).when(root).join(firstPath, joinType == null ? JoinType.INNER : joinType);
        doReturn(result).when(second).get(secondPath);

        return root;
    }

    public static Root<?> createRootWithAssociationAttribute(String firstPath, String secondPath, Path<?> result) {
        Attribute<?, ?> attribute = mock(Attribute.class);
        Root<?> root = createRootWithAttribute(firstPath, attribute);
        Join<?, ?> second = mock(Join.class);

        doReturn(true).when(attribute).isAssociation();
        doReturn(Set.of(second)).when(root).getJoins();
        doReturn(attribute).when(second).getAttribute();
        doReturn(firstPath).when(attribute).getName();
        doReturn(result).when(second).get(secondPath);

        return root;
    }

    private static Root<?> createRootWithAttribute(String attributeName, Attribute<?, ?> attribute) {
        EntityType<?> entityType = mock(EntityType.class);
        Root<?> root = mock(Root.class);

        doReturn(attribute).when(entityType).getAttribute(attributeName);
        doReturn(entityType).when(root).getModel();

        return root;
    }
}
