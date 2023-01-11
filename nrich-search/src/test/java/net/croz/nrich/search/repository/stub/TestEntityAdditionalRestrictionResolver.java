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

import lombok.RequiredArgsConstructor;
import net.croz.nrich.search.api.model.AdditionalRestrictionResolver;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class TestEntityAdditionalRestrictionResolver implements AdditionalRestrictionResolver<TestEntity, TestEntity, TestEntitySearchRequest> {

    private final boolean appendAdditionalRestriction;

    @Override
    public List<Predicate> resolvePredicateList(CriteriaBuilder criteriaBuilder, CriteriaQuery<TestEntity> query, Root<TestEntity> root, TestEntitySearchRequest request) {
        if (appendAdditionalRestriction) {
            return Collections.singletonList(criteriaBuilder.greaterThan(root.get("age"), 100));
        }

        return null;
    }
}
