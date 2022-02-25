package net.croz.nrich.search.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QueryUtilTest {

    @Test
    void shouldConvertToCountResult() {
        // given
        List<Long> results = Arrays.asList(null, 1L, 10L, null);

        // when
        Long result = QueryUtil.toCountResult(results);

        // then
        assertThat(result).isEqualTo(11L);
    }

}
