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

import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

public class ConstrainedSessionLocaleResolver extends SessionLocaleResolver {

    private final String defaultLocaleCode;

    private final List<String> supportedLocaleCodeList;

    public ConstrainedSessionLocaleResolver(String defaultLocaleCode, List<String> supportedLocaleCodeList) {
        this.defaultLocaleCode = defaultLocaleCode == null ? Locale.getDefault().toLanguageTag() : defaultLocaleCode;
        this.supportedLocaleCodeList = supportedLocaleCodeList;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        Locale localeToSet = locale;
        if (locale == null || !supportedLocaleCodeList.contains(locale.toString())) {
            localeToSet = Locale.forLanguageTag(defaultLocaleCode);
        }

        WebUtils.setSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME, localeToSet);
    }
}
