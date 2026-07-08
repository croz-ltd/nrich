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

import org.springframework.http.ProblemDetail;

/**
 * Contributor that mutates the {@link ProblemDetail} body before it is serialized. The advice
 * invokes registered contributors uniformly for every exception inside {@code handleExceptionInternal}.
 *
 * <p>Implementations may also implement {@link org.springframework.core.Ordered}. Default nrich
 * contributors do so via constants defined in
 * {@code net.croz.nrich.problemdetail.constant.ProblemDetailConstants}.
 *
 * <p>Scope: contributors mutate the body (type / title / detail / extension properties), <strong>not</strong>
 * the HTTP status - status is resolved in the funnel before contributors run and synced into
 * {@code pd.setStatus(...)}.
 */
public interface ProblemDetailContributor {

    void contribute(ProblemDetail problemDetail, ProblemDetailContributorContext context);

}
