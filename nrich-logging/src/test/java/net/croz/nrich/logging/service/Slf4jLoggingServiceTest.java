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

package net.croz.nrich.logging.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import net.croz.nrich.logging.api.model.LoggingLevel;
import net.croz.nrich.logging.api.model.LoggingVerbosityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static net.croz.nrich.logging.testutil.Slf4jLoggingServiceGeneratingUtil.createAndInitListAppender;
import static net.croz.nrich.logging.testutil.Slf4jLoggingServiceGeneratingUtil.createAuxiliaryExceptionData;
import static net.croz.nrich.logging.testutil.Slf4jLoggingServiceGeneratingUtil.returnsFirstMessageCode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class Slf4jLoggingServiceTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private Slf4jLoggingService loggingService;

    private final ListAppender<ILoggingEvent> appender = createAndInitListAppender();

    @BeforeEach
    void setup() {
        lenient().doAnswer(returnsFirstMessageCode()).when(messageSource).getMessage(any(MessageSourceResolvable.class), any(Locale.class));

        appender.list.clear();
    }

    @Test
    void shouldLogInternalExceptionWithNoConfiguration() {
        // given
        RuntimeException exception = new RuntimeException("exception");

        // when
        loggingService.logInternalException(exception, null);
        ILoggingEvent event = getLastEvent();

        // then
        assertThat(event.getLevel()).isEqualTo(Level.ERROR);
        assertThat(event.getMessage()).isEqualTo(
            "Information about the exception above: [className: java.lang.RuntimeException], [message: exception], [additionalInfoData: ]"
        );
    }

    @Test
    void shouldLogInternalExceptionWithNoConfigurationAndWithAuxiliaryData() {
        // given
        String uuid = UUID.randomUUID().toString();
        Instant occurrenceTime = Instant.now();

        RuntimeException exception = new RuntimeException("exception");
        Map<String, Object> exceptionAuxiliaryData = new LinkedHashMap<>();
        exceptionAuxiliaryData.put("uuid", uuid);
        exceptionAuxiliaryData.put("occurrenceTime", occurrenceTime);

        // when
        loggingService.logInternalException(exception, exceptionAuxiliaryData);
        ILoggingEvent event = getLastEvent();

        // then
        assertThat(event.getLevel()).isEqualTo(Level.ERROR);
        assertThat(event.getMessage()).isEqualTo(
            "Information about the exception above: [className: java.lang.RuntimeException], [message: exception], [additionalInfoData: uuid: %s, occurrenceTime: %s]".formatted(uuid, occurrenceTime)
        );
    }

    @Test
    void shouldIncludeAuxiliaryDataWhenLoggingException() {
        // given
        Map<String, String> auxiliaryExceptionData = createAuxiliaryExceptionData();
        RuntimeException exception = new RuntimeException("exception");

        // when
        loggingService.logInternalException(exception, auxiliaryExceptionData);

        // then
        assertThat(getLastEvent().getMessage()).contains("key: value");
    }

    @EnumSource
    @ParameterizedTest
    void shouldLogOnConfiguredLevel(LoggingLevel loggingLevel) {
        // given
        IllegalArgumentException exception = new IllegalArgumentException("exception");

        doReturn(loggingLevel.name()).when(messageSource).getMessage(
            argThat(argument -> Arrays.stream(Objects.requireNonNull(argument.getCodes())).anyMatch(code -> code.contains(exception.getClass().getName()))),
            any(Locale.class)
        );

        // when
        loggingService.logInternalException(exception, null);
        ILoggingEvent event = getLastEvent();

        // then
        assertThat(event.getLevel()).isEqualTo(Level.valueOf(loggingLevel.name()));
    }

    @Test
    void shouldLogOnCompactVerbosityLevel() {
        // given
        IllegalStateException exception = new IllegalStateException("exception");

        doReturn(LoggingVerbosityLevel.COMPACT.name()).when(messageSource).getMessage(
            argThat(argument -> Arrays.stream(Objects.requireNonNull(argument.getCodes())).anyMatch(code -> code.contains(exception.getClass().getName()))),
            any(Locale.class)
        );

        // when
        loggingService.logInternalException(exception, null);
        ILoggingEvent event = getLastEvent();

        // then
        assertThat(event.getMessage()).contains("Exception occurred: [className: java.lang.IllegalStateException], [message: exception]");
    }

    @Test
    void shouldSkipLoggingOnNoneVerbosityLevel() {
        // given
        IllegalStateException exception = new IllegalStateException("exception");

        mockMessageSourceCall(LoggingVerbosityLevel.NONE.name(), exception.getClass().getName());

        // when
        loggingService.logInternalException(exception, null);

        // then
        assertThat(appender.list).isEmpty();
    }

    @Test
    void shouldNotFailWhenExceptionIsNull() {
        // when
        Throwable thrown = catchThrowable(() -> loggingService.logInternalException(null, null));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldSkippLoggingWhenLoggingVerbosityLevelIsNone() {
        // when
        loggingService.logInternalException(new IllegalArgumentException(), LoggingLevel.ERROR, LoggingVerbosityLevel.NONE, null);

        // then
        assertThat(appender.list).isEmpty();
    }

    @MethodSource("shouldLogOnSpecifiedLoggingAndVerbosityLevelMethodSource")
    @ParameterizedTest
    void shouldLogOnSpecifiedLoggingAndVerbosityLevel(LoggingLevel loggingLevel, LoggingVerbosityLevel loggingVerbosityLevel, Level expectedLevel, String expectedMessage) {
        // given
        IllegalStateException exception = new IllegalStateException("exception");

        mockMessageSourceCall(LoggingVerbosityLevel.FULL.name(), ".loggingVerbosityLevel");
        mockMessageSourceCall(LoggingLevel.ERROR.name(), ".loggingLevel");

        // when
        loggingService.logInternalException(exception, loggingLevel, loggingVerbosityLevel, null);
        ILoggingEvent event = getLastEvent();


        // then
        assertThat(event.getLevel()).isEqualTo(expectedLevel);
        assertThat(event.getMessage()).isEqualTo(expectedMessage);
    }

    private static Stream<Arguments> shouldLogOnSpecifiedLoggingAndVerbosityLevelMethodSource() {
        return Stream.of(
            Arguments.of(LoggingLevel.ERROR, LoggingVerbosityLevel.FULL, Level.ERROR, "Information about the exception above: [className: java.lang.IllegalStateException], [message: exception], [additionalInfoData: ]"),
            Arguments.of(LoggingLevel.DEBUG, LoggingVerbosityLevel.COMPACT, Level.DEBUG, "Exception occurred: [className: java.lang.IllegalStateException], [message: exception], [additionalInfoData: ]"),
            Arguments.of(null, null, Level.ERROR, "Information about the exception above: [className: java.lang.IllegalStateException], [message: exception], [additionalInfoData: ]")
        );
    }

    @Test
    void shouldLogExternalException() {
        // given
        String exceptionClassName = "com.external.Exception";
        String exceptionMessage = "exceptionMessage";

        // when
        loggingService.logExternalException(exceptionClassName, exceptionMessage, null);
        ILoggingEvent event = getLastEvent();

        // then
        assertThat(event.getLevel()).isEqualTo(Level.ERROR);
        assertThat(event.getMessage()).contains(exceptionClassName).contains(exceptionMessage);
    }

    @Test
    void shouldSkipLoggingWhenNoClassNameHasBeenGiven() {
        // given
        String exceptionMessage = "exceptionMessage";

        // when
        loggingService.logExternalException(null, exceptionMessage, null);

        // then
        assertThat(appender.list).isEmpty();
    }

    private ILoggingEvent getLastEvent() {
        assertThat(appender.list).isNotEmpty();

        return appender.list.get(appender.list.size() - 1);
    }

    private void mockMessageSourceCall(String returnValue, String expectedCode) {
        lenient().doReturn(returnValue).when(messageSource).getMessage(
            argThat(argument -> Arrays.stream(Objects.requireNonNull(argument.getCodes())).anyMatch(code -> code.contains(expectedCode))),
            any(Locale.class)
        );
    }
}
