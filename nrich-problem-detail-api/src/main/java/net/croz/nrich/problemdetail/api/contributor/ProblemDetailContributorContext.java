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

package net.croz.nrich.problemdetail.api.contributor;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Locale;

/**
 * Per-request context supplied to every {@link ProblemDetailContributor} invocation. Carries the
 * exception, the request, the resolved status, and identifiers generated once in the funnel
 * (so the wire {@code errorId} matches the correlation id written to the exception log).
 */
public interface ProblemDetailContributorContext {

    Exception exception();

    WebRequest request();

    HttpStatusCode status();

    Locale locale();

    /**
     * UUID generated once per exception in the funnel; shared by the error-id contributor (wire)
     * and the handler's exception logging (log) so the two correlate without ordering coupling.
     *
     * @return correlation identifier for this exception
     */
    String correlationId();

    Instant timestamp();

}
