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

package net.croz.nrich.registry.configuration.stub;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.io.Serializable;

@Setter
@Getter
@IdClass(RegistryConfigurationTestEntityWithIdClass.RegistryConfigurationTestEntityWithIdClassId.class)
@Entity
public class RegistryConfigurationTestEntityWithIdClass {

    @Id
    private Long firstId;

    @Id
    private Long secondId;

    private String name;

    @Setter
    @Getter
    @EqualsAndHashCode
    static class RegistryConfigurationTestEntityWithIdClassId implements Serializable {

        private Long firstId;

        private Long secondId;

    }
}
