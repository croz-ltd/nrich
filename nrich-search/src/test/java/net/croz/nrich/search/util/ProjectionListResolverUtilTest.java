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

package net.croz.nrich.search.util;

import net.croz.nrich.search.api.model.SearchProjection;
import net.croz.nrich.search.util.stub.ProjectionListResolverUtilTestEntity;
import net.croz.nrich.search.util.stub.ProjectionListResolverUtilTestEntityWithSuperclass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ProjectionListResolverUtilTest {

    @Test
    void shouldNotFailWhenNoAnnotationsAreAvailable() {
        // when
        Throwable thrown = catchThrowable(() -> ProjectionListResolverUtil.resolveSearchProjectionList(String.class));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldResolveSearchProjectionList() {
        // when
        List<SearchProjection<ProjectionListResolverUtilTestEntity>> result = ProjectionListResolverUtil.resolveSearchProjectionList(ProjectionListResolverUtilTestEntity.class);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting("path").containsExactly("name", "nestedEntity.nestedEntityName", "nestedEntity.id", "nestedEntity.anotherName");
        assertThat(result).extracting("alias").containsExactly("name", "nestedName", "nestedId", "conditionalName");

        // and when
        SearchProjection<ProjectionListResolverUtilTestEntity> conditionalProjection = result.stream()
            .filter(projection -> "conditionalName".equals(projection.getAlias()))
            .findFirst()
            .orElse(null);

        // then
        assertThat(conditionalProjection).isNotNull();
        assertThat(conditionalProjection.getCondition().test(null)).isTrue();
        assertThat(conditionalProjection.getCondition().test(new ProjectionListResolverUtilTestEntity())).isFalse();
    }

    @Test
    void shouldResolveSearchProjectionListFromSuperclass() {
        // when
        List<SearchProjection<ProjectionListResolverUtilTestEntity>> result = ProjectionListResolverUtil.resolveSearchProjectionList(ProjectionListResolverUtilTestEntityWithSuperclass.class);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).extracting("path").containsExactly("otherName", "name", "nestedEntity.nestedEntityName", "nestedEntity.id", "nestedEntity.anotherName");
        assertThat(result).extracting("alias").containsExactly("otherName", "name", "nestedName", "nestedId", "conditionalName");
    }
}
