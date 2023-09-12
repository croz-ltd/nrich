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

package net.croz.nrich.search.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.criteria.JoinType;
import java.util.function.Predicate;

/**
 * Join or join fetch that will be applied to query if condition is satisfied (it condition is null join will always be applied).
 *
 * @param <R> search request
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SearchJoin<R> {

    /**
     * Association path in relation to root entity.
     */
    private final String path;

    /**
     * Association alias, will be applied only if fetch is false.
     */
    private String alias;

    /**
     * Type of join (inner or left).
     */
    private JoinType joinType;

    /**
     * Condition that decides should join be applied.
     */
    private Predicate<R> condition;

    /**
     * Whether join or join fetch is applied.
     */
    private boolean fetch;

    public static <R> SearchJoin<R> innerJoin(String path) {
        return new SearchJoin<>(path, path, JoinType.INNER, null, false);
    }

    public static <R> SearchJoin<R> leftJoin(String path) {
        return new SearchJoin<>(path, path, JoinType.LEFT, null, false);
    }

    public static <R> SearchJoin<R> innerJoinFetch(String path) {
        return new SearchJoin<>(path, path, JoinType.INNER, null, true);
    }

    public static <R> SearchJoin<R> leftJoinFetch(String path) {
        return new SearchJoin<>(path, path, JoinType.LEFT, null, true);
    }
}
