package net.croz.nrich.webmvc.localeresolver;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Arrays;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class ConstrainedSessionLocaleResolverTest {

    @Test
    void shouldOnlyAllowSettingOfAllowedLocale() {
        // given
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver("hr", Arrays.asList("hr", "en"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(new MockHttpSession());

        // when
        constrainedSessionLocaleResolver.setLocale(request, new MockHttpServletResponse(), Locale.CHINA);

        // then
        assertThat(request.getSession()).isNotNull();
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)).isEqualTo(Locale.forLanguageTag("hr"));

        // and when
        constrainedSessionLocaleResolver.setLocale(request, new MockHttpServletResponse(), Locale.ENGLISH);

        // then
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void shouldSetDefaultLocaleWhenSettingNullLocale() {
        // given
        ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver(null, Arrays.asList("hr", "zh_CN", "en"));

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setSession(new MockHttpSession());
        Locale.setDefault(Locale.UK);

        // when
        constrainedSessionLocaleResolver.setLocale(request, new MockHttpServletResponse(), null);

        // then
        assertThat(request.getSession()).isNotNull();
        assertThat(request.getSession().getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME)).isEqualTo(Locale.UK);
    }
}
