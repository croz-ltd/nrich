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
        MultipartFile file = new MockMultipartFile("**.txt", FILE_BYTES);
        ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(file);

        // when
        Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldReportErrorWhenFileExtensionIsNotValidForMultipartFile() {
        // given
        MultipartFile file = new MockMultipartFile("1.exe", FILE_BYTES);
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
        MultipartFile file = new MockMultipartFile(filename, FILE_BYTES);
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
