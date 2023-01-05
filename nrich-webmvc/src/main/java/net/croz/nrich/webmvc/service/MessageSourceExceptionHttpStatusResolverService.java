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

package net.croz.nrich.webmvc.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.webmvc.api.service.ExceptionHttpStatusResolverService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;

@RequiredArgsConstructor
public class MessageSourceExceptionHttpStatusResolverService implements ExceptionHttpStatusResolverService {

    private static final String PREFIX_FORMAT = "%s.%s";

    private static final String EXCEPTION_HTTP_STATUS_SUFFIX = "httpStatus";

    private final MessageSource messageSource;

    @Override
    public Integer resolveHttpStatusForException(Exception exception) {
        String statusMessageCode = String.format(PREFIX_FORMAT, exception.getClass().getName(), EXCEPTION_HTTP_STATUS_SUFFIX);

        DefaultMessageSourceResolvable defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(statusMessageCode);

        Integer status = null;
        try {
            status = Integer.valueOf(messageSource.getMessage(defaultMessageSourceResolvable, LocaleContextHolder.getLocale()));
        }
        catch (Exception ignored) {
            // ignored
        }

        return status;
    }
}
