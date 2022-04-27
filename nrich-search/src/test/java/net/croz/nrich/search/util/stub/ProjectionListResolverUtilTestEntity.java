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

package net.croz.nrich.search.util.stub;

import lombok.Getter;
import lombok.Setter;
import net.croz.nrich.search.api.annotation.Projection;
import org.springframework.beans.factory.annotation.Value;

import java.util.function.Predicate;

@Setter
@Getter
public class ProjectionListResolverUtilTestEntity {

    private static String STATIC_PROPERTY = "value";

    private String name;

    private transient String transientProperty;

    @Projection(path = "nestedEntity.nestedEntityName")
    private String nestedName;

    @Value("nestedEntity.id")
    private Long nestedId;

    @Projection(path = "nestedEntity.anotherName", condition = Condition.class)
    private String conditionalName;

    public static class Condition implements Predicate<ProjectionListResolverUtilTestEntity> {
        @Override
        public boolean test(ProjectionListResolverUtilTestEntity entity) {
            return entity == null;
        }
    }
}
