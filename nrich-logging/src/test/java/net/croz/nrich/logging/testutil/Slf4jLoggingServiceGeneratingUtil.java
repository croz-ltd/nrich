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

package net.croz.nrich.logging.testutil;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import net.croz.nrich.logging.service.Slf4jLoggingService;
import org.mockito.stubbing.Answer;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;

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
        return Map.of("key", "value");
    }
}

