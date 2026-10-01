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

import net.croz.nrich.problemdetail.constant.ProblemDetailConstants;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static net.croz.nrich.problemdetail.testutil.ProblemDetailGeneratingUtil.CORRELATION_ID;
import static net.croz.nrich.problemdetail.testutil.ProblemDetailGeneratingUtil.createProblemDetail;
import static net.croz.nrich.problemdetail.testutil.ProblemDetailGeneratingUtil.createProblemDetailContext;
import static org.assertj.core.api.Assertions.assertThat;

class ErrorIdProblemDetailContributorTest {

    private final ErrorIdProblemDetailContributor contributor = new ErrorIdProblemDetailContributor();

    @Test
    void shouldSetErrorIdPropertyFromContextCorrelationId() {
        // given
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR);

        // when
        contributor.contribute(problemDetail, createProblemDetailContext(new RuntimeException("exception")));

        // then
        assertThat(problemDetail.getProperties()).containsEntry(ProblemDetailConstants.ERROR_ID_PROPERTY, CORRELATION_ID);
    }

    @Test
    void shouldExposeConfiguredOrder() {
        // expect
        assertThat(contributor.getOrder()).isEqualTo(ProblemDetailConstants.ERROR_ID_CONTRIBUTOR_ORDER);
    }

}
