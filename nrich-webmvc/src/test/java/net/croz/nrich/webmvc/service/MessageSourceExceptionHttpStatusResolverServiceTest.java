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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MessageSourceExceptionHttpStatusResolverServiceTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MessageSourceExceptionHttpStatusResolverService messageSourceExceptionHttpStatusResolverService;

    @Test
    void shouldReturnNullWhenNoStatusHasBeenDefined() {
        // when
        Integer result = messageSourceExceptionHttpStatusResolverService.resolveHttpStatusForException(new RuntimeException());

        // then
        assertThat(result).isNull();
    }

    @Test
    void shouldReturnDefinedStatus() {
        // given
        DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable("java.lang.RuntimeException.httpStatus");

        doReturn("404").when(messageSource).getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());

        // when
        Integer result = messageSourceExceptionHttpStatusResolverService.resolveHttpStatusForException(new RuntimeException());

        // then
        assertThat(result).isEqualTo(404);
    }
}
