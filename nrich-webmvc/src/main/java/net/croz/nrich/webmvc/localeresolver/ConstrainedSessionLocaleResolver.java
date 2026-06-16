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

package net.croz.nrich.webmvc.localeresolver;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class ConstrainedSessionLocaleResolver extends SessionLocaleResolver {

    private final Locale defaultLocale;

    private final Set<Locale> supportedLocaleSet;

    public ConstrainedSessionLocaleResolver(String defaultLocaleCode, List<String> supportedLocaleCodeList) {
        this.defaultLocale = defaultLocaleCode == null ? Locale.getDefault() : StringUtils.parseLocale(defaultLocaleCode);
        this.supportedLocaleSet = supportedLocaleCodeList.stream()
            .map(StringUtils::parseLocale)
            .collect(Collectors.toSet());
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale resolvedLocale = super.resolveLocale(request);

        return isSupported(resolvedLocale) ? resolvedLocale : defaultLocale;
    }

    @Override
    public LocaleContext resolveLocaleContext(HttpServletRequest request) {
        LocaleContext localeContext = super.resolveLocaleContext(request);
        if (isSupported(localeContext.getLocale())) {
            return localeContext;
        }

        return new SimpleTimeZoneAwareLocaleContext(defaultLocale, resolveTimeZone(localeContext));
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        Locale localeToSet = isSupported(locale) ? locale : defaultLocale;

        WebUtils.setSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME, localeToSet);
    }

    @Override
    public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
        boolean localeSupported = localeContext != null && isSupported(localeContext.getLocale());
        LocaleContext localeContextToSet = localeSupported ? localeContext : new SimpleTimeZoneAwareLocaleContext(defaultLocale, resolveTimeZone(localeContext));

        super.setLocaleContext(request, response, localeContextToSet);
    }

    private boolean isSupported(Locale locale) {
        return supportedLocaleSet.contains(locale);
    }

    private TimeZone resolveTimeZone(LocaleContext localeContext) {
        return localeContext instanceof TimeZoneAwareLocaleContext timeZoneAwareLocaleContext ? timeZoneAwareLocaleContext.getTimeZone() : null;
    }
}
