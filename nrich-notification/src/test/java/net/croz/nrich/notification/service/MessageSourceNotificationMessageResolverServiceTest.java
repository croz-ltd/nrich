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

package net.croz.nrich.notification.service;

import net.croz.nrich.notification.stub.MessageSourceNotificationMessageResolverServiceTestRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

import static net.croz.nrich.notification.testutil.DefaultMessageSourceResolvableGeneratingUtil.createMessageSourceNotificationMessageResolverServiceTestRequestFieldErrorLabelMessageSourceResolvable;
import static net.croz.nrich.notification.testutil.DefaultMessageSourceResolvableGeneratingUtil.createMessageSourceNotificationMessageResolverServiceTestRequestFieldErrorMessageSourceResolvable;
import static net.croz.nrich.notification.testutil.DefaultMessageSourceResolvableGeneratingUtil.createMessageSourceNotificationMessageResolverServiceTestRequestObjectErrorMessageSourceResolvable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MessageSourceNotificationMessageResolverServiceTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MessageSourceNotificationMessageResolverService messageResolverService;

    @Test
    void shouldResolveMessage() {
        // given
        String[] codeList = { "message.code" };
        Object[] argumentList = { "argument" };
        DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(codeList, argumentList);
        String message = "message";

        doReturn(message).when(messageSource).getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());

        // when
        String result = messageResolverService.resolveMessage(List.of(codeList), List.of(argumentList), null);

        // then
        assertThat(result).isEqualTo(message);
    }

    @Test
    void shouldNotFailOnNullArguments() {
        // when
        Throwable thrown = catchThrowable(() -> messageResolverService.resolveMessage(List.of("message.code"), null, null));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldResolveMessageForObjectError() {
        // given
        Class<?> requestType = MessageSourceNotificationMessageResolverServiceTestRequest.class;
        Object[] arguments = new Object[0];
        ObjectError error = new ObjectError("target", new String[] { "code" }, arguments, "message");
        DefaultMessageSourceResolvable messageSourceResolvable = createMessageSourceNotificationMessageResolverServiceTestRequestObjectErrorMessageSourceResolvable(arguments);
        String message = "message";

        doReturn(message).when(messageSource).getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());

        // when
        String result = messageResolverService.resolveMessageForObjectError(requestType, error);

        // then
        assertThat(result).isEqualTo(message);
    }

    @Test
    void shouldResolveMessageForFieldError() {
        // given
        Class<?> requestType = MessageSourceNotificationMessageResolverServiceTestRequest.class;
        ObjectError error = new FieldError("target", "field", "invalid value", true, new String[] { "code" }, null, "message");
        DefaultMessageSourceResolvable fieldErrorMessageSourceResolvable = createMessageSourceNotificationMessageResolverServiceTestRequestFieldErrorMessageSourceResolvable();
        DefaultMessageSourceResolvable fieldErrorLabelMessageSourceResolvable = createMessageSourceNotificationMessageResolverServiceTestRequestFieldErrorLabelMessageSourceResolvable();
        String message = "message";
        String messageLabel = "Field error";

        doReturn(message).when(messageSource).getMessage(fieldErrorMessageSourceResolvable, LocaleContextHolder.getLocale());
        doReturn(messageLabel).when(messageSource).getMessage(fieldErrorLabelMessageSourceResolvable, LocaleContextHolder.getLocale());

        // when
        String result = messageResolverService.resolveMessageForObjectError(requestType, error);

        // then
        assertThat(result).isEqualTo(messageLabel + ": " + message);
    }

    @Test
    void shouldConvertArrayArgumentsString() {
        // given
        Class<?> requestType = MessageSourceNotificationMessageResolverServiceTestRequest.class;
        Object[] arguments = { new Object[] { "first", "second" } };
        ObjectError error = new ObjectError("target", new String[] { "code" }, arguments, "message");
        DefaultMessageSourceResolvable messageSourceResolvable = createMessageSourceNotificationMessageResolverServiceTestRequestObjectErrorMessageSourceResolvable(new Object[] { "first, second" });
        String message = "message";

        doReturn(message).when(messageSource).getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());

        // when
        String result = messageResolverService.resolveMessageForObjectError(requestType, error);

        // then
        assertThat(result).isEqualTo(message);
    }
}
