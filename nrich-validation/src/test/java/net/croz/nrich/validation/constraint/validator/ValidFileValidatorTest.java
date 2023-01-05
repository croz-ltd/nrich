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
import net.croz.nrich.validation.constraint.stub.ValidFileValidatorFilePartTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidFileValidatorInvalidTypeFileTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidFileValidatorMultipartFileTestRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static net.croz.nrich.validation.constraint.testutil.ValidationConstraintGeneratingUtil.filePart;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(ValidationTestConfiguration.class)
class ValidFileValidatorTest {

    private static final byte[] FILE_BYTES = "content".getBytes(StandardCharsets.UTF_8);

    @Autowired
    private Validator validator;

    @Test
    void shouldReportErrorWhenFileNameIsNotValidForMultipartFile() {
        // given
        MultipartFile file = new MockMultipartFile("file", "**.txt", null, FILE_BYTES);
        ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(file);

        // when
        Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldReportErrorWhenFileExtensionIsNotValidForMultipartFile() {
        // given
        MultipartFile file = new MockMultipartFile("file", "1.exe", null, FILE_BYTES);
        ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(file);

        // when
        Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldReportErrorWhenContentTypeIsNotValidForMultipartFile() {
        // given
        MultipartFile file = new MockMultipartFile("1.txt", "1.txt", "application/json", FILE_BYTES);
        ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(file);

        // when
        Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @ValueSource(strings = { "1.txt", "1", "c:\\1.txt", "c:/1.txt" })
    @ParameterizedTest
    void shouldNotReportErrorForValidMultipartFile(String filename) {
        // given
        MultipartFile file = new MockMultipartFile("file", filename, null, FILE_BYTES);
        ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(file);

        // when
        Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorForNullFile() {
        // given
        ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(null);

        // when
        Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldThrowExceptionForUnrecognizedFileType() {
        // given
        ValidFileValidatorInvalidTypeFileTestRequest request = new ValidFileValidatorInvalidTypeFileTestRequest(new Object());

        // when
        Throwable thrown = catchThrowable(() -> validator.validate(request));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldValidateFilePart() {
        // given
        ValidFileValidatorFilePartTestRequest request = new ValidFileValidatorFilePartTestRequest(filePart("1.txt", MediaType.TEXT_PLAIN));

        // when
        Set<ConstraintViolation<ValidFileValidatorFilePartTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }
}
