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

package net.croz.nrich.problemdetail.testutil;

import net.croz.nrich.problemdetail.api.contributor.ProblemDetailContributorContext;
import net.croz.nrich.problemdetail.contributor.DefaultProblemDetailContributorContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Locale;

public final class ProblemDetailGeneratingUtil {

    public static final String CORRELATION_ID = "00000000-0000-0000-0000-000000000001";

    private static final Instant TIMESTAMP = Instant.parse("2026-05-28T10:15:30Z");

    private static final String REQUEST_PATH = "/api/orders";

    private ProblemDetailGeneratingUtil() {
    }

    public static WebRequest createWebRequest() {
        return new ServletWebRequest(new MockHttpServletRequest("GET", REQUEST_PATH));
    }

    public static ProblemDetailContributorContext createProblemDetailContext(Exception exception) {
        return createProblemDetailContext(exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ProblemDetailContributorContext createProblemDetailContext(Exception exception, HttpStatusCode status) {
        return new DefaultProblemDetailContributorContext(exception, createWebRequest(), status, Locale.ENGLISH, CORRELATION_ID, TIMESTAMP);
    }

    public static ProblemDetail createProblemDetail(HttpStatus status) {
        return ProblemDetail.forStatus(status);
    }

}
