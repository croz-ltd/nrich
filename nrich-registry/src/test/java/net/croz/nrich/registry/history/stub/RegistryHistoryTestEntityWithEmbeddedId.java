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

package net.croz.nrich.registry.history.stub;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Audited
@Entity
public class RegistryHistoryTestEntityWithEmbeddedId {

    @EmbeddedId
    private RegistryHistoryTestEntityWithEmbeddedIdPrimaryKey id;

    private BigDecimal amount;

    @Setter
    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class RegistryHistoryTestEntityWithEmbeddedIdPrimaryKey implements Serializable {

        private Long firstId;

        private Long secondId;

        public Map<String, Long> asMap() {
            Map<String, Long> mapKey = new HashMap<>();

            mapKey.put("firstId", firstId);
            mapKey.put("secondId", secondId);

            return mapKey;
        }

    }
}
