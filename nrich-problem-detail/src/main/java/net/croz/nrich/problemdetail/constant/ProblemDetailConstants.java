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

package net.croz.nrich.problemdetail.constant;

public final class ProblemDetailConstants {

    public static final String MESSAGES_RESOURCE_BUNDLE = "nrich-problem-detail-messages";

    public static final String STATUS_CODE_FORMAT = "problemDetail.nrich.status.%s";

    public static final String CODE_CODE_FORMAT = "problemDetail.nrich.code.%s";

    public static final String SEVERITY_CODE_FORMAT = "problemDetail.nrich.severity.%s";

    public static final String LOGGING_LEVEL_CODE_FORMAT = "problemDetail.nrich.logging.level.%s";

    public static final String LOGGING_VERBOSITY_CODE_FORMAT = "problemDetail.nrich.logging.verbosity.%s";

    public static final String SPRING_DETAIL_CODE_FORMAT = "problemDetail.%s";

    public static final String TITLE_CODE_FORMAT = "problemDetail.title.%s";

    public static final String TYPE_CODE_FORMAT = "problemDetail.type.%s";

    public static final String DEFAULT_DETAIL_CODE = "problemDetail.nrich.default-detail";

    public static final String DEFAULT_TITLE_CODE = "problemDetail.nrich.default-title";

    public static final String VALIDATION_DETAIL_CODE = "problemDetail.nrich.validation-detail";

    public static final String VALIDATION_TITLE_CODE = "problemDetail.nrich.validation-title";

    public static final String VALIDATION_FQCN_CODE_FORMAT = "%s.%s.%s";

    public static final String VALIDATION_CONSTRAINT_FIELD_FORMAT = "%s.%s";

    public static final String ERRORS_PROPERTY = "errors";

    public static final String CODE_PROPERTY = "code";

    public static final String SEVERITY_PROPERTY = "severity";

    public static final String ERROR_ID_PROPERTY = "errorId";

    public static final String TIMESTAMP_PROPERTY = "timestamp";

    public static final String LOG_ERROR_ID_KEY = "errorId";

    public static final String LOG_PATH_KEY = "path";

    public static final String LOG_METHOD_KEY = "method";

    public static final String SEVERITY_ERROR = "ERROR";

    public static final String SEVERITY_WARNING = "WARNING";

    public static final String SEVERITY_INFO = "INFO";

    public static final String DEFAULT_VALIDATION_DETAIL = "Validation failed.";

    public static final String DEFAULT_ERROR_DETAIL = "Error occurred.";

    public static final int VALIDATION_ERRORS_CONTRIBUTOR_ORDER = 100;

    public static final int CODE_CONTRIBUTOR_ORDER = 200;

    public static final int SEVERITY_CONTRIBUTOR_ORDER = 300;

    public static final int ERROR_ID_CONTRIBUTOR_ORDER = 400;

    public static final int TIMESTAMP_CONTRIBUTOR_ORDER = 500;

    private ProblemDetailConstants() {
    }

}
