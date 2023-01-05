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

package net.croz.nrich.logging.constant;

public final class LoggingConstants {

    public static final String EXCEPTION_COMPACT_LEVEL_LOG_FORMAT = "Exception occurred: [className: %s], message: %s, additionalInfoData: %s";

    public static final String EXCEPTION_FULL_LEVEL_MESSAGE_FORMAT = "Exception occurred: [className: %s], message: %s";

    public static final String EXCEPTION_FULL_LEVEL_LOG_FORMAT = "---------------- Information about above exception %s: %s ----------------";

    public static final String AUXILIARY_DATA_FORMAT = "%s: %s";

    public static final String LOGGING_VERBOSITY_LEVEL_RESOLVING_FORMAT = "%s.loggingVerbosityLevel";

    public static final String LOGGING_LEVEL_RESOLVING_FORMAT = "%s.loggingLevel";

    private LoggingConstants() {
    }
}
