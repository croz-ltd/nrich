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

package net.croz.nrich.search.repository.stub;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;

@Setter
@Getter
@Entity
public class TestEntityWithEmbeddedId {

    @EmbeddedId
    private TestEntityWithEmbeddedIdObjectId id;

    private String name;

    @Setter
    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class TestEntityWithEmbeddedIdObjectId implements Serializable {

        @ManyToOne
        private RegistryHistoryTestEntityWithEmbeddedObjectFirstKey firstKey;

        @ManyToOne
        private RegistryHistoryTestEntityWithEmbeddedObjectSecondKey secondKey;

    }

    @Setter
    @Getter
    @Entity
    public static class RegistryHistoryTestEntityWithEmbeddedObjectFirstKey {

        @GeneratedValue
        @Id
        private Long id;

    }

    @Setter
    @Getter
    @Entity
    public static class RegistryHistoryTestEntityWithEmbeddedObjectSecondKey {

        @GeneratedValue
        @Id
        private Long id;

    }
}
