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

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Audited
@Entity
public class RegistryHistoryTestEntityWithEmbeddedObject {

    @EmbeddedId
    private RegistryHistoryTestEntityWithEmbeddedObjectId id;

    private BigDecimal amount;

    @Setter
    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class RegistryHistoryTestEntityWithEmbeddedObjectId implements Serializable {

        @ManyToOne
        private RegistryHistoryTestEntityWithEmbeddedObjectFirstKey firstKey;

        @ManyToOne
        private RegistryHistoryTestEntityWithEmbeddedObjectSecondKey secondKey;

        public Map<String, Object> asMap() {
            Map<String, Object> mapKey = new HashMap<>();

            mapKey.put("firstKey", firstKey);
            mapKey.put("secondKey", secondKey);

            return mapKey;
        }

    }
}
