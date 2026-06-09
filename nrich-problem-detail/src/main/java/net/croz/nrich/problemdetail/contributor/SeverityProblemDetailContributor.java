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

package net.croz.nrich.problemdetail.contributor;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.problemdetail.api.contributor.ProblemDetailContributor;
import net.croz.nrich.problemdetail.api.contributor.ProblemDetailContributorContext;
import net.croz.nrich.problemdetail.constant.ProblemDetailConstants;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

@RequiredArgsConstructor
public class SeverityProblemDetailContributor implements ProblemDetailContributor, Ordered {

    private final MessageSource messageSource;

    @Override
    public void contribute(ProblemDetail problemDetail, ProblemDetailContributorContext context) {
        String key = String.format(ProblemDetailConstants.SEVERITY_CODE_FORMAT, context.exception().getClass().getName());
        String resolved = messageSource.getMessage(key, null, defaultSeverity(context.status()), context.locale());

        problemDetail.setProperty(ProblemDetailConstants.SEVERITY_PROPERTY, resolved);
    }

    @Override
    public int getOrder() {
        return ProblemDetailConstants.SEVERITY_CONTRIBUTOR_ORDER;
    }

    private String defaultSeverity(HttpStatusCode status) {
        if (status.is5xxServerError()) {
            return ProblemDetailConstants.SEVERITY_ERROR;
        }
        if (status.is4xxClientError()) {
            return ProblemDetailConstants.SEVERITY_WARNING;
        }

        return ProblemDetailConstants.SEVERITY_INFO;
    }

}
