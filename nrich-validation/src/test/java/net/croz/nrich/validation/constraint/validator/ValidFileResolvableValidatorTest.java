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

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
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
        ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(new MockMultipartFile("**.txt", FILE_BYTES));

        // when
        Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldReportErrorWhenFileExtensionIsNotValidForMultipartFile() {
        // given
        ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(new MockMultipartFile("someFile.exe", FILE_BYTES));

        // when
        Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldReportErrorWhenContentTypeIsNotValidForMultipartFile() {
        // given
        ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(new MockMultipartFile("someFile.txt", "someFile.txt", MediaType.APPLICATION_JSON_VALUE, FILE_BYTES));

        // when
        Set<ConstraintViolation<ValidFileResolvableValidatorMultipartFileTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldNotReportErrorForValidMultipartFileWithCustomConstraints() {
        // given
        ValidFileResolvableValidatorMultipartFileCustomTestRequest request = new ValidFileResolvableValidatorMultipartFileCustomTestRequest(new MockMultipartFile("someFile.pdf", "someFile.pdf", MediaType.APPLICATION_PDF_VALUE, FILE_BYTES));

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
        ValidFileResolvableValidatorMultipartFileTestRequest request = new ValidFileResolvableValidatorMultipartFileTestRequest(new MockMultipartFile(fileName, FILE_BYTES));

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
        ValidFileResolvableValidatorEmptyPropertyTestRequest request = new ValidFileResolvableValidatorEmptyPropertyTestRequest(new MockMultipartFile("someFile.txt", FILE_BYTES));

        // when
        Set<ConstraintViolation<ValidFileResolvableValidatorEmptyPropertyTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }
}
