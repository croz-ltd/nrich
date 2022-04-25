package net.croz.nrich.search.util;

import org.junit.jupiter.api.Test;

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
    void shouldCalculateFullPath() {
        // given
        String path = "first";
        Path<?> first = mock(Path.class);
        Path<?> second = mock(Path.class);

        doReturn(second).when(first).get(path);

        // when
        Path<?> result = PathResolvingUtil.calculateFullPath(first, new String[] { path });

        // then
        assertThat(result).isEqualTo(second);
    }
}
