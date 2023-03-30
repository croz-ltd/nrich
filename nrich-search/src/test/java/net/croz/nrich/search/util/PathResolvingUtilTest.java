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

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import java.util.Arrays;
import java.util.List;

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
        List<String> path = Arrays.asList("first", "second");
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

    @Test
    void shouldCalculateFullRestrictionPath() {
        // given
        String firstPath = "restriction";
        String secondPath = "attribute";
        From<?, ?> first = mock(From.class);
        Join<?, ?> second = mock(Join.class);
        Path<?> third = mock(Path.class);

        doReturn(second).when(first).join(firstPath);
        doReturn(third).when(second).get(secondPath);

        // when
        Path<?> result = PathResolvingUtil.calculateFullRestrictionPath(first, new String[] { firstPath, secondPath });

        // then
        assertThat(result).isEqualTo(third);
    }

    @Test
    void shouldCalculateFullSelectionPath() {
        // given
        String firstPath = "selection";
        String secondPath = "attribute";
        Path<?> first = mock(From.class);
        Path<?> second = mock(Path.class);
        Path<?> third = mock(Path.class);

        doReturn(second).when(first).get(firstPath);
        doReturn(third).when(second).get(secondPath);

        // when
        Path<?> result = PathResolvingUtil.calculateFullSelectionPath(first, new String[] { firstPath, secondPath });

        // then
        assertThat(result).isEqualTo(third);
    }
}
