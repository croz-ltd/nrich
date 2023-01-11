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

package net.croz.nrich.search.api.repository;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * When using Hibernate as JPA provider allows for searching by properties annotated with @NaturalId annotation,
 * these queries can be better cached allowing for a single database query.
 *
 * @param <T> entity type
 */
public interface NaturalIdSearchExecutor<T> {

    /**
     * Returns a single entity with specified simple naturalId or {@link Optional#empty()} if none was found.
     * Used when single property is annotated with @NaturalId annotation.
     *
     * @param naturalId value of the property annotated with @NaturalId annotation
     * @param <I>       type of natural id property
     * @return single entity matching conditions or {@link Optional#empty()} if none was found.
     */
    <I extends Serializable> Optional<T> findBySimpleNaturalId(I naturalId);

    /**
     * Returns a single entity with specified naturalId or {@link Optional#empty()} if none was found.
     * Used when multiple properties are annotated with @NaturalId annotation.
     *
     * @param naturalId map containing property name/value pairs of the properties annotated with @NaturalId annotation
     * @return single entity matching conditions or {@link Optional#empty()} if none was found.
     */
    Optional<T> findByNaturalId(Map<String, Object> naturalId);

}
