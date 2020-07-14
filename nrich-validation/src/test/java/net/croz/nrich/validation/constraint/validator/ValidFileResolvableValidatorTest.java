package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.ValidFileResolvableValidatorFilePartTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidFileResolvableValidatorInvalidTypeFileTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidFileResolvableValidatorMultipartFileCustomTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidFileResolvableValidatorMultipartFileTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static net.croz.nrich.validation.constraint.testutil.ValidationConstraintGeneratingUtil.filePart;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(ValidationTestConfiguration.class)
@TestPropertySource("classpath:application.properties")
public class ValidFileResolvableValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldReportErrorWhenFileNameIsNotValidForMultipartFile() {
        // given
        final ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(new MockMultipartFile("**.txt", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldReportErrorWhenFileExtensionIsNotValidForMultipartFile() {
        // given
        final ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(new MockMultipartFile("someFile.exe", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldReportErrorWhenContentTypeIsNotValidForMultipartFile() {
        // given
        final ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(new MockMultipartFile("someFile.txt", "someFile.txt", MediaType.APPLICATION_JSON_VALUE, "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldNotReportErrorForValidMultipartFile() {
        // given
        final ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(new MockMultipartFile("someFile.txt", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorForValidMultipartFileWithCustomConstraints() {
        // given
        final ValidFileResolvableValidatorMultipartFileCustomTestRequest request = new ValidFileResolvableValidatorMultipartFileCustomTestRequest(new MockMultipartFile("someFile.pdf", "someFile.pdf", MediaType.APPLICATION_PDF_VALUE, "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileCustomTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorForNullFile() {
        // given
        final ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(null);

        // when
        final Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldThrowExceptionForUnrecognizedFileType() {
        // given
        final ValidFileResolvableValidatorInvalidTypeFileTestRequest request = new ValidFileResolvableValidatorInvalidTypeFileTestRequest(new Object());

        // when
        final Throwable thrown = catchThrowable(() -> validator.validate(request));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotReportExtensionErrorForFileWithoutExtensionMultipartFile() {
        // given
        final ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(new MockMultipartFile("someFile", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldExtractFileNameWithoutWindowsStylePath() {
        // given
        final ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(new MockMultipartFile("c:\\someFile.txt", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldExtractFileNameWithoutUnixStylePath() {
        // given
        final ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(new MockMultipartFile("c:/someFile.txt", "content".getBytes()));

        // when
        final Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldValidateFilePart() {
        // given
        final ValidFileResolvableValidatorFilePartTestRequest request = new ValidFileResolvableValidatorFilePartTestRequest(filePart("someFile.txt", MediaType.TEXT_PLAIN));

        // when
        final Set<ConstraintViolation<ValidFileResolvableValidatorFilePartTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }
}
