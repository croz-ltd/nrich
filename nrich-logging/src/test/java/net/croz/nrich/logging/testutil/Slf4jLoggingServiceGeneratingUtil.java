package net.croz.nrich.logging.testutil;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import net.croz.nrich.logging.service.Slf4jLoggingService;
import org.mockito.stubbing.Answer;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Slf4jLoggingServiceGeneratingUtil {

    private Slf4jLoggingServiceGeneratingUtil() {
    }

    public static ListAppender<ILoggingEvent> createAndInitListAppender() {
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(Slf4jLoggingService.class);
        logger.addAppender(appender);

        return appender;
    }

    public static Answer<String> returnsFirstMessageCode() {
        return invocation -> {
            MessageSourceResolvable messageSourceResolvable = invocation.getArgument(0);

            return Objects.requireNonNull(messageSourceResolvable.getCodes())[0];
        };
    }

    public static Map<String, String> createAuxiliaryExceptionData() {
        Map<String, String> auxiliaryExceptionData = new HashMap<>();

        auxiliaryExceptionData.put("key", "value");

        return auxiliaryExceptionData;
    }
}

