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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.Locale;
import java.util.stream.Stream;

import static net.croz.nrich.problemdetail.testutil.ProblemDetailGeneratingUtil.createProblemDetail;
import static net.croz.nrich.problemdetail.testutil.ProblemDetailGeneratingUtil.createProblemDetailContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class SeverityProblemDetailContributorTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private SeverityProblemDetailContributor contributor;

    @Test
    void shouldSetSeverityResolvedFromMessageSource() {
        // given
        RuntimeException exception = new RuntimeException("exception");
        String key = String.format(ProblemDetailConstants.SEVERITY_CODE_FORMAT, exception.getClass().getName());
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR);

        doReturn("CUSTOM").when(messageSource).getMessage(key, null, ProblemDetailConstants.SEVERITY_ERROR, Locale.ENGLISH);

        // when
        contributor.contribute(problemDetail, createProblemDetailContext(exception, HttpStatus.INTERNAL_SERVER_ERROR));

        // then
        assertThat(problemDetail.getProperties()).containsEntry(ProblemDetailConstants.SEVERITY_PROPERTY, "CUSTOM");
    }

    @MethodSource("shouldSetSeverityFromHttpStatusWhenNoOverrideExistsMethodSource")
    @ParameterizedTest
    void shouldSetSeverityFromHttpStatusWhenNoOverrideExists(HttpStatus status, String expectedSeverity) {
        // given
        RuntimeException exception = new RuntimeException("exception");
        String key = String.format(ProblemDetailConstants.SEVERITY_CODE_FORMAT, exception.getClass().getName());
        ProblemDetail problemDetail = createProblemDetail(status);

        doReturn(expectedSeverity).when(messageSource).getMessage(key, null, expectedSeverity, Locale.ENGLISH);

        // when
        contributor.contribute(problemDetail, createProblemDetailContext(exception, status));

        // then
        assertThat(problemDetail.getProperties()).containsEntry(ProblemDetailConstants.SEVERITY_PROPERTY, expectedSeverity);
    }

    private static Stream<Arguments> shouldSetSeverityFromHttpStatusWhenNoOverrideExistsMethodSource() {
        return Stream.of(
            Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, ProblemDetailConstants.SEVERITY_ERROR),
            Arguments.of(HttpStatus.BAD_REQUEST, ProblemDetailConstants.SEVERITY_WARNING),
            Arguments.of(HttpStatus.SEE_OTHER, ProblemDetailConstants.SEVERITY_INFO)
        );
    }

    @Test
    void shouldExposeConfiguredOrder() {
        // expect
        assertThat(contributor.getOrder()).isEqualTo(ProblemDetailConstants.SEVERITY_CONTRIBUTOR_ORDER);
    }

}
