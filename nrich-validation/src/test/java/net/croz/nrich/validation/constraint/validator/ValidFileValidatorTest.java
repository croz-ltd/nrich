package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.ValidFileValidatorFilePartTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidFileValidatorInvalidTypeFileTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidFileValidatorMultipartFileTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static net.croz.nrich.validation.constraint.testutil.ValidationConstraintGeneratingUtil.filePart;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(ValidationTestConfiguration.class)
public class ValidFileValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldReportErrorWhenFileNameIsNotValidForMultipartFile() {
        // given
        final ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(new MockMultipartFile("**.txt", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldReportErrorWhenFileExtensionIsNotValidForMultipartFile() {
        // given
        final ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(new MockMultipartFile("1.exe", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldReportErrorWhenContentTypeIsNotValidForMultipartFile() {
        // given
        final ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(new MockMultipartFile("1.txt", "1.txt", "application/json", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldNotReportErrorForValidMultipartFile() {
        // given
        final ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(new MockMultipartFile("1.txt", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorForNullFile() {
        // given
        final ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(null);

        // when
        final Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldThrowExceptionForUnrecognizedFileType() {
        // given
        final ValidFileValidatorInvalidTypeFileTestRequest request = new ValidFileValidatorInvalidTypeFileTestRequest(new Object());

        // when
        final Throwable thrown = catchThrowable(() -> validator.validate(request));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotReportExtensionErrorForFileWithoutExtensionMultipartFile() {
        // given
        final ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(new MockMultipartFile("1", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldExtractFileNameWithoutWindowsStylePath() {
        // given
        final ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(new MockMultipartFile("c:\\1.txt", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldExtractFileNameWithoutUnixStylePath() {
        // given
        final ValidFileValidatorMultipartFileTestRequest request = new ValidFileValidatorMultipartFileTestRequest(new MockMultipartFile("c:/1.txt", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldValidateFilePart() {
        // given
        final ValidFileValidatorFilePartTestRequest request = new ValidFileValidatorFilePartTestRequest(filePart("1.txt", MediaType.TEXT_PLAIN));

        // when
        final Set<ConstraintViolation<ValidFileValidatorFilePartTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }
}
