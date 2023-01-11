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

package net.croz.nrich.registry.data.testutil;

import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroup;
import net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroupId;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithIdClass;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

public final class RegistryDataResolvingUtil {

    private RegistryDataResolvingUtil() {
    }

    public static RegistryTestEntityWithIdClass findRegistryTestEntityWithIdClass(EntityManager entityManager, RegistryTestEntityWithIdClass registryTestEntityWithIdClass) {
        try {
            return (RegistryTestEntityWithIdClass) entityManager.createQuery("from RegistryTestEntityWithIdClass where firstId = :firstId and secondId = :secondId")
                .setParameter("firstId", registryTestEntityWithIdClass.getFirstId())
                .setParameter("secondId", registryTestEntityWithIdClass.getSecondId())
                .getSingleResult();
        }
        catch (NoResultException ignored) {
            return null;
        }
    }

    public static RegistryTestEmbeddedUserGroup findRegistryTestEmbeddedUserGroup(EntityManager entityManager, RegistryTestEmbeddedUserGroupId groupId) {
        try {
            return (RegistryTestEmbeddedUserGroup) entityManager.createQuery("from RegistryTestEmbeddedUserGroup where userGroupId.user.id = :userId and userGroupId.group.id = :groupId")
                .setParameter("userId", groupId.getUser().getId())
                .setParameter("groupId", groupId.getGroup().getId())
                .getSingleResult();
        }
        catch (NoResultException ignored) {
            return null;
        }
    }
}
