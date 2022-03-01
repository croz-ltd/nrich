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
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
        assertThat(event.getMessage()).isEqualTo("---------------- Information about above exception Exception occurred: [className: java.lang.RuntimeException], message: exception:  ----------------");
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
        assertThat(event.getMessage()).contains("Exception occurred: [className: java.lang.IllegalStateException], message: exception");
    }

    @Test
    void shouldSkipLoggingOnNoneVerbosityLevel() {
        // given
        IllegalStateException exception = new IllegalStateException("exception");

        doReturn(LoggingVerbosityLevel.NONE.name()).when(messageSource).getMessage(
                argThat(argument -> Arrays.stream(Objects.requireNonNull(argument.getCodes())).anyMatch(code -> code.contains(exception.getClass().getName()))),
                any(Locale.class)
        );

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
}
