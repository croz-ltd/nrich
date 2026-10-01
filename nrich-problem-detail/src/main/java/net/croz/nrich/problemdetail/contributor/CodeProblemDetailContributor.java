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
import net.croz.nrich.core.api.exception.ExceptionWithMessageCode;
import net.croz.nrich.problemdetail.api.contributor.ProblemDetailContributor;
import net.croz.nrich.problemdetail.api.contributor.ProblemDetailContributorContext;
import net.croz.nrich.problemdetail.constant.ProblemDetailConstants;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.http.ProblemDetail;

@RequiredArgsConstructor
public class CodeProblemDetailContributor implements ProblemDetailContributor, Ordered {

    private final MessageSource messageSource;

    private final boolean fallbackToClassName;

    @Override
    public void contribute(ProblemDetail problemDetail, ProblemDetailContributorContext context) {
        Exception exception = context.exception();
        String key = String.format(ProblemDetailConstants.CODE_CODE_FORMAT, exception.getClass().getName());

        String fallback = null;
        if (exception instanceof ExceptionWithMessageCode exceptionWithMessageCode) {
            fallback = exceptionWithMessageCode.getMessageCode();
        }
        else if (fallbackToClassName) {
            fallback = exception.getClass().getName();
        }

        String resolved = messageSource.getMessage(key, null, fallback, context.locale());
        if (resolved != null) {
            problemDetail.setProperty(ProblemDetailConstants.CODE_PROPERTY, resolved);
        }
    }

    @Override
    public int getOrder() {
        return ProblemDetailConstants.CODE_CONTRIBUTOR_ORDER;
    }

}
