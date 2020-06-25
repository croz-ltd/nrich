package net.croz.nrich.webmvc.localeresolver;

import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

public class ConstrainedSessionLocaleResolver extends SessionLocaleResolver {

    private final String defaultLocaleCode;

    private final List<String> supportedLocaleCodeList;

    public ConstrainedSessionLocaleResolver(final String defaultLocaleCode, final List<String> supportedLocaleCodeList) {
        this.defaultLocaleCode = defaultLocaleCode == null ? Locale.getDefault().toLanguageTag() : defaultLocaleCode;
        this.supportedLocaleCodeList = supportedLocaleCodeList;
    }

    public void setLocale(final HttpServletRequest request, final HttpServletResponse response, final Locale locale) {

        Locale localeToSet = locale;
        if (locale == null || !supportedLocaleCodeList.contains(locale.toString())) {
            localeToSet = Locale.forLanguageTag(defaultLocaleCode);
        }

        WebUtils.setSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME, localeToSet);
    }
}
