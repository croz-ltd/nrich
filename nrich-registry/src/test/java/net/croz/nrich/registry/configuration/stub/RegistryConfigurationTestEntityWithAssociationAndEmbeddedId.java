/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Entity
public class RegistryConfigurationTestEntityWithAssociationAndEmbeddedId {

    @EmbeddedId
    private RegistryConfigurationTestEntityPrimaryKey id;

    private BigDecimal amount;

    @OneToOne
    private RegistryConfigurationTestEntity registryConfigurationTestEntityOneToOne;

    @ManyToOne
    private RegistryConfigurationTestEntity registryConfigurationTestEntityManyToOne;

    @OneToMany
    private List<RegistryConfigurationTestEntity> registryConfigurationTestEntityList;

    @Setter
    @Getter
    @EqualsAndHashCode
    @Embeddable
    static class RegistryConfigurationTestEntityPrimaryKey implements Serializable {

        private Long firstId;

        private Long secondId;

    }
}
