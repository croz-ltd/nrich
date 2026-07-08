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

import static net.croz.nrich.problemdetail.testutil.ProblemDetailGeneratingUtil.createProblemDetail;
import static net.croz.nrich.problemdetail.testutil.ProblemDetailGeneratingUtil.createProblemDetailContext;
import static org.assertj.core.api.Assertions.assertThat;

class TimestampProblemDetailContributorTest {

    private final TimestampProblemDetailContributor contributor = new TimestampProblemDetailContributor();

    @Test
    void shouldSetTimestampPropertyFromContext() {
        // given
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.BAD_REQUEST);

        // when
        contributor.contribute(problemDetail, createProblemDetailContext(new IllegalStateException("exception"), HttpStatus.BAD_REQUEST));

        // then
        assertThat(problemDetail.getProperties()).containsEntry(ProblemDetailConstants.TIMESTAMP_PROPERTY, "2026-05-28T10:15:30Z");
    }

    @Test
    void shouldExposeConfiguredOrder() {
        // expect
        assertThat(contributor.getOrder()).isEqualTo(ProblemDetailConstants.TIMESTAMP_CONTRIBUTOR_ORDER);
    }

}
