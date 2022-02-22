package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.NotNullWhenInvalidTestRequest;
import net.croz.nrich.validation.constraint.stub.NotNullWhenTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(ValidationTestConfiguration.class)
class NotNullWhenValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldThrowExceptionWhenSpecifiedPropertyDoesntExist() {
        // given
        NotNullWhenInvalidTestRequest request = new NotNullWhenInvalidTestRequest("property", "different property");

        // when
        Throwable thrown = catchThrowable(() -> validator.validate(request));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotReportErrorWhenPropertyIsNullAndConditionIsFalse() {
        // given
        NotNullWhenTestRequest request = new NotNullWhenTestRequest(null, "different property");

        // when
        Set<ConstraintViolation<NotNullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorWhenPropertyIsNotNullAndConditionIsTrue() {
        // given
        NotNullWhenTestRequest request = new NotNullWhenTestRequest("value", "not null");

        // when
        Set<ConstraintViolation<NotNullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldReportErrorWhenPropertyIsNullAndConditionIsTrue() {
        // given
        NotNullWhenTestRequest request = new NotNullWhenTestRequest(null, "not null");

        // when
        Set<ConstraintViolation<NotNullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }
}
