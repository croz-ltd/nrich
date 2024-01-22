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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.util.List;

import static net.croz.nrich.search.util.testutil.PathResolvingUtilGeneratingUtil.createRootWithAssociationAttribute;
import static net.croz.nrich.search.util.testutil.PathResolvingUtilGeneratingUtil.createRootWithCollectionAttribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class PathResolvingUtilTest {

    @Test
    void shouldConvertToPathList() {
        // given
        String path = "first.second";

        // when
        String[] result = PathResolvingUtil.convertToPathList(path);

        // then
        assertThat(result).containsExactly("first", "second");
    }

    @Test
    void shouldJoinPath() {
        // given
        String[] path = { "first", "second" };

        // when
        String result = PathResolvingUtil.joinPath(path);

        // then
        assertThat(result).isEqualTo("first.second");
    }

    @Test
    void shouldJoinPathList() {
        // given
        List<String> path = List.of("first", "second");
        String currentPath = "third";

        // when
        String result = PathResolvingUtil.joinPath(path, currentPath);

        // then
        assertThat(result).isEqualTo("first.second.third");
    }

    @Test
    void shouldRemoveFirstPathElement() {
        // given
        String[] path = { "first", "second" };

        // when
        String result = PathResolvingUtil.removeFirstPathElement(path);

        // then
        assertThat(result).isEqualTo("second");
    }

    @EnumSource(JoinType.class)
    @NullSource
    @ParameterizedTest
    void shouldCalculateFullPath(JoinType joinType) {
        // given
        String firstPath = "restriction";
        String secondPath = "attribute";
        Path<?> expectedResult = mock(Path.class);
        Root<?> first = createRootWithCollectionAttribute(firstPath, secondPath, expectedResult, joinType);

        // when
        Path<?> result = PathResolvingUtil.calculateFullPath(first, joinType, new String[] { firstPath, secondPath });

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldReuseExistingJoinWhenCalculatingPath() {
        // given
        String firstPath = "restriction";
        String secondPath = "attribute";
        Path<?> expectedResult = mock(Path.class);

        Root<?> first = createRootWithAssociationAttribute(firstPath, secondPath, expectedResult);

        // when
        Path<?> result = PathResolvingUtil.calculateFullPath(first, null, new String[] { firstPath, secondPath });

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void shouldNotJoinClassAttribute() {
        // given
        String firstPath = "class";
        Root<?> first = mock(Root.class);
        Path<?> second = mock(Path.class);

        doReturn(second).when(first).get(firstPath);

        // when
        Path<?> result = PathResolvingUtil.calculateFullPath(first, null, new String[] { firstPath });

        // then
        assertThat(result).isEqualTo(second);
    }
}
