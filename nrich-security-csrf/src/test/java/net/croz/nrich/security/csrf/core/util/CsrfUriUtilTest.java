package net.croz.nrich.security.csrf.core.util;

import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static net.croz.nrich.security.csrf.core.testutil.CsrfCoreGeneratingUtil.csrfExcludeConfig;
import static org.assertj.core.api.Assertions.assertThat;

class CsrfUriUtilTest {

    @Test
    void shouldReturnFalseWhenExcludedUriConfigurationIsEmpty() {
        // when
        boolean result = CsrfUriUtil.excludeUri(null, "url");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenExcludedUriIsNotMatched() {
        // given
        CsrfExcludeConfig csrfExcludeConfig = csrfExcludeConfig("not-matched", null);

        // when
        boolean result = CsrfUriUtil.excludeUri(Collections.singletonList(csrfExcludeConfig), "url");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenExcludedUriIsNotMatchedByRegex() {
        // given
        CsrfExcludeConfig csrfExcludeConfig = csrfExcludeConfig(null, "/*");

        // when
        boolean result = CsrfUriUtil.excludeUri(Collections.singletonList(csrfExcludeConfig), "url");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueWhenExcludedUriIsMatched() {
        // given
        CsrfExcludeConfig csrfExcludeConfig = csrfExcludeConfig("url", null);

        // when
        boolean result = CsrfUriUtil.excludeUri(Collections.singletonList(csrfExcludeConfig), "url");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenExcludedUriIsMatchedByRegex() {
        // given
        CsrfExcludeConfig csrfExcludeConfig = csrfExcludeConfig(null, ".*");

        // when
        boolean result = CsrfUriUtil.excludeUri(Collections.singletonList(csrfExcludeConfig), "url");

        // then
        assertThat(result).isTrue();
    }
}
