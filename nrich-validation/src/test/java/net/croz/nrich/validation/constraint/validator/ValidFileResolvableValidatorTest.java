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

package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.ValidFileResolvableValidatorEmptyPropertyTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidFileResolvableValidatorFilePartTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidFileResolvableValidatorInvalidTypeFileTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidFileResolvableValidatorMultipartFileCustomTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidFileResolvableValidatorMultipartFileTestRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static net.croz.nrich.validation.constraint.testutil.ValidationConstraintGeneratingUtil.filePart;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(ValidationTestConfiguration.class)
@TestPropertySource("classpath:application.properties")
class ValidFileResolvableValidatorTest {

    private static final byte[] FILE_BYTES = "content".getBytes(StandardCharsets.UTF_8);

    @Autowired
    private Validator validator;

    @Test
    void shouldReportErrorWhenFileNameIsNotValidForMultipartFile() {
        // given
        MultipartFile file = new MockMultipartFile("file", "**.txt", null, FILE_BYTES);
        ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(file);

        // when
        Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldReportErrorWhenFileExtensionIsNotValidForMultipartFile() {
        // given
        MultipartFile file = new MockMultipartFile("file", "someFile.exe", null, FILE_BYTES);
        ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(file);

        // when
        Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldReportErrorWhenContentTypeIsNotValidForMultipartFile() {
        // given
        MockMultipartFile file = new MockMultipartFile("someFile.txt", "someFile.txt", MediaType.APPLICATION_JSON_VALUE, FILE_BYTES);
        ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(file);

        // when
        Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldNotReportErrorForValidMultipartFileWithCustomConstraints() {
        // given
        MockMultipartFile file = new MockMultipartFile("someFile.pdf", "someFile.pdf", MediaType.APPLICATION_PDF_VALUE, FILE_BYTES);
        ValidFileResolvableValidatorMultipartFileCustomTestRequest request = new ValidFileResolvableValidatorMultipartFileCustomTestRequest(file);

        // when
        Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileCustomTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorForNullFile() {
        // given
        ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(null);

        // when
        Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldThrowExceptionForUnrecognizedFileType() {
        // given
        ValidFileResolvableValidatorInvalidTypeFileTestRequest request = new ValidFileResolvableValidatorInvalidTypeFileTestRequest(new Object());

        // when
        Throwable thrown = catchThrowable(() -> validator.validate(request));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @ValueSource(strings = { "someFile.txt", "someFile", "c:\\someFile.txt", "c:/someFile.txt" })
    @ParameterizedTest
    void shouldValidateMultipartFilename(String fileName) {
        // given
        MultipartFile file = new MockMultipartFile("file", fileName, null, FILE_BYTES);
        ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(file);

        // when
        Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldValidateFilePart() {
        // given
        ValidFileResolvableValidatorFilePartTestRequest request = new ValidFileResolvableValidatorFilePartTestRequest(filePart("someFile.txt", MediaType.TEXT_PLAIN));

        // when
        Set<ConstraintViolation<ValidFileResolvableValidatorFilePartTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotFailOnEmptyPropertyNames() {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "someFile.txt", null, FILE_BYTES);
        ValidFileResolvableValidatorEmptyPropertyTestRequest request = new ValidFileResolvableValidatorEmptyPropertyTestRequest(file);

        // when
        Set<ConstraintViolation<ValidFileResolvableValidatorEmptyPropertyTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }
}
