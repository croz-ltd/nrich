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
import net.croz.nrich.problemdetail.contributor.stub.CodeProblemDetailContributorTestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.Locale;

import static net.croz.nrich.problemdetail.testutil.ProblemDetailGeneratingUtil.createProblemDetail;
import static net.croz.nrich.problemdetail.testutil.ProblemDetailGeneratingUtil.createProblemDetailContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class CodeProblemDetailContributorTest {

    @Mock
    private MessageSource messageSource;

    private CodeProblemDetailContributor contributor;

    @BeforeEach
    void setup() {
        contributor = new CodeProblemDetailContributor(messageSource, false);
    }

    @Test
    void shouldSetCodePropertyResolvedFromMessageSource() {
        // given
        IllegalArgumentException exception = new IllegalArgumentException("exception");
        String key = String.format(ProblemDetailConstants.CODE_CODE_FORMAT, exception.getClass().getName());
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.BAD_REQUEST);
        String messageCode = "BAD_INPUT_FROM_EXCEPTION";

        doReturn(messageCode).when(messageSource).getMessage(key, null, null, Locale.ENGLISH);

        // when
        contributor.contribute(problemDetail, createProblemDetailContext(exception, HttpStatus.BAD_REQUEST));

        // then
        assertThat(problemDetail.getProperties()).containsEntry(ProblemDetailConstants.CODE_PROPERTY, messageCode);
    }

    @Test
    void shouldFallBackToExceptionWithMessageCodeWhenMessageSourceHasNoMatch() {
        // given
        CodeProblemDetailContributorTestException exception = new CodeProblemDetailContributorTestException();
        String key = String.format(ProblemDetailConstants.CODE_CODE_FORMAT, exception.getClass().getName());
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR);
        String messageCode = "BAD_INPUT_FROM_CODE";

        doReturn(messageCode).when(messageSource).getMessage(key, null, CodeProblemDetailContributorTestException.MESSAGE_CODE, Locale.ENGLISH);

        // when
        contributor.contribute(problemDetail, createProblemDetailContext(exception));

        // then
        assertThat(problemDetail.getProperties()).containsEntry(ProblemDetailConstants.CODE_PROPERTY, messageCode);
    }

    @Test
    void shouldNotSetCodeWhenNothingResolves() {
        // given
        RuntimeException exception = new RuntimeException("exception");
        String key = String.format(ProblemDetailConstants.CODE_CODE_FORMAT, exception.getClass().getName());
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR);

        doReturn(null).when(messageSource).getMessage(key, null, null, Locale.ENGLISH);

        // when
        contributor.contribute(problemDetail, createProblemDetailContext(exception));

        // then
        assertThat(problemDetail.getProperties()).isNullOrEmpty();
    }

    @Test
    void shouldFallBackToClassNameWhenConfiguredAndNoMessageResolves() {
        // given
        CodeProblemDetailContributor classNameFallbackContributor = new CodeProblemDetailContributor(messageSource, true);
        RuntimeException exception = new RuntimeException("exception");
        String key = String.format(ProblemDetailConstants.CODE_CODE_FORMAT, exception.getClass().getName());
        String className = exception.getClass().getName();
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR);

        doReturn(className).when(messageSource).getMessage(key, null, className, Locale.ENGLISH);

        // when
        classNameFallbackContributor.contribute(problemDetail, createProblemDetailContext(exception));

        // then
        assertThat(problemDetail.getProperties()).containsEntry(ProblemDetailConstants.CODE_PROPERTY, className);
    }

    @Test
    void shouldPreferMessageCodeOverClassNameFallbackWhenConfigured() {
        // given
        CodeProblemDetailContributor classNameFallbackContributor = new CodeProblemDetailContributor(messageSource, true);
        CodeProblemDetailContributorTestException exception = new CodeProblemDetailContributorTestException();
        String key = String.format(ProblemDetailConstants.CODE_CODE_FORMAT, exception.getClass().getName());
        ProblemDetail problemDetail = createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR);

        doReturn(CodeProblemDetailContributorTestException.MESSAGE_CODE).when(messageSource).getMessage(key, null, CodeProblemDetailContributorTestException.MESSAGE_CODE, Locale.ENGLISH);

        // when
        classNameFallbackContributor.contribute(problemDetail, createProblemDetailContext(exception));

        // then
        assertThat(problemDetail.getProperties()).containsEntry(ProblemDetailConstants.CODE_PROPERTY, CodeProblemDetailContributorTestException.MESSAGE_CODE);
    }

    @Test
    void shouldExposeConfiguredOrder() {
        // expect
        assertThat(contributor.getOrder()).isEqualTo(ProblemDetailConstants.CODE_CONTRIBUTOR_ORDER);
    }

}
