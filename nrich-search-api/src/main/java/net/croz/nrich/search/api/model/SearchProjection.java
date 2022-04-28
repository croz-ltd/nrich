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

package net.croz.nrich.search.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

/**
 * Projection that will be applied to query. Prefer using result class but if result class is not needed this can be used as an alternative.
 *
 * @param <R> search request
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SearchProjection<R> {

    /**
     * Path to property in relation to root entity.
     */
    private final String path;

    /**
     * Projection alias.
     */
    private String alias;

    /**
     * Condition that decides if projection should be applied.
     */
    private Predicate<R> condition;

}
