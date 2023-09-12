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

package net.croz.nrich.registry.data.stub;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Embeddable
public class RegistryTestEmbeddedUserGroupId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    private RegistryTestEmbeddedUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    private RegistryTestEmbeddedGroup group;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof RegistryTestEmbeddedUserGroupId)) {
            return false;
        }

        RegistryTestEmbeddedUserGroupId that = (RegistryTestEmbeddedUserGroupId) object;

        return Objects.equals(user.getId(), that.user.getId()) && Objects.equals(group.getId(), that.group.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId(), group.getId());
    }
}
