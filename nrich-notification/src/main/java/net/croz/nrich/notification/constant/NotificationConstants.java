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

package net.croz.nrich.notification.constant;

public final class NotificationConstants {

    public static final String PREFIX_MESSAGE_FORMAT = "%s.%s";

    public static final String CONSTRAINT_SHORT_MESSAGE_FORMAT = "%s.%s.%s";

    public static final String CONSTRAINT_FULL_MESSAGE_FORMAT = "%s.%s.%s.%s";

    public static final String FIELD_ERROR_FORMAT = "%s: %s";

    public static final String INVALID_SUFFIX = "invalid";

    public static final String FIELD_LABEL_SUFFIX = "label";

    public static final String MESSAGE_TITLE_SUFFIX = "title";

    public static final String MESSAGE_CONTENT_SUFFIX = "content";

    public static final String MESSAGE_SEVERITY_SUFFIX = "severity";

    public static final String EMPTY_MESSAGE = "";

    public static final String SUCCESS_MESSAGE_TITLE_CODE = "notification.success.title";

    public static final String SUCCESS_DEFAULT_CODE = "notification.success.default-message";

    public static final String VALIDATION_FAILED_MESSAGE_TITLE_CODE = "notification.validation-failed.title";

    public static final String VALIDATION_FAILED_CONTENT_CODE = "notification.validation-failed.content";

    public static final String ERROR_OCCURRED_MESSAGE_TITLE_CODE = "notification.error-occurred.title";

    public static final String ERROR_OCCURRED_DEFAULT_CODE = "notification.error-occurred.default-message";

    public static final String ADDITIONAL_EXCEPTION_DATA_MESSAGE_CODE_FORMAT = "notification.additional-data.%s.message";

    public static final String UNDEFINED_MESSAGE_VALUE = "UNDEFINED";

    public static final String UNKNOWN_VALIDATION_TARGET = "UNKNOWN_VALIDATION_TARGET";

    public static final String EMPTY_NAME = "";

    public static final char LEFT_BRACKET = '[';

    public static final char RIGHT_BRACKET = ']';

    public static final char SPACE = ' ';

    public static final char REQUEST_PATH_SEPARATOR = '/';

    public static final char MESSAGE_PATH_SEPARATOR = '.';

    private NotificationConstants() {
    }
}
