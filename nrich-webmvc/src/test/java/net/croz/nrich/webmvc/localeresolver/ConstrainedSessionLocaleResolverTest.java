package net.croz.nrich.webmvc.localeresolver;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Arrays;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstrainedSessionLocaleResolverTest {

    @Test
    void shouldOnlyAllowSettingOfAllowedLocale() {
        // given
        final MockHttpSession session = new MockHttpSession();
        final MockHttpServletRequest request = new MockHttpServletRequest();

        request.setSession(session);

        final ConstrainedSessionLocaleResolver constrainedSessionLocaleResolver = new ConstrainedSessionLocaleResolver("hr", Arrays.asList("hr", "en"));

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
}
