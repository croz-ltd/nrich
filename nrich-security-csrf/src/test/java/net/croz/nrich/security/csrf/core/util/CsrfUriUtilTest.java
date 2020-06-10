package net.croz.nrich.security.csrf.core.util;

import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static net.croz.nrich.security.csrf.core.testutil.CsrfCoreGeneratingUtil.csrfExcludeConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class CsrfUriUtilTest {

    @Test
    void shouldReturnFalseWhenExcludedUriConfigurationIsEmpty() {
        // when
        final boolean result = CsrfUriUtil.excludeUri(null, "url");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenExcludedUriIsNotMatched() {
        // given
        final CsrfExcludeConfig csrfExcludeConfig = csrfExcludeConfig("not-matched", null);

        // when
        final boolean result = CsrfUriUtil.excludeUri(Collections.singletonList(csrfExcludeConfig), "url");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenExcludedUriIsNotMatchedByRegex() {
        // given
        final CsrfExcludeConfig csrfExcludeConfig = csrfExcludeConfig(null, "/*");

        // when
        final boolean result = CsrfUriUtil.excludeUri(Collections.singletonList(csrfExcludeConfig), "url");

        // then
        assertThat(result).isFalse();
    }


    @Test
    void shouldReturnTrueWhenExcludedUriIsMatched() {
        // given
        final CsrfExcludeConfig csrfExcludeConfig = csrfExcludeConfig("url", null);

        // when
        final boolean result = CsrfUriUtil.excludeUri(Collections.singletonList(csrfExcludeConfig), "url");

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnTrueWhenExcludedUriIsMatchedByRegex() {
        // given
        final CsrfExcludeConfig csrfExcludeConfig = csrfExcludeConfig(null, ".*");

        // when
        final boolean result = CsrfUriUtil.excludeUri(Collections.singletonList(csrfExcludeConfig), "url");

        // then
        assertThat(result).isTrue();
    }
}
