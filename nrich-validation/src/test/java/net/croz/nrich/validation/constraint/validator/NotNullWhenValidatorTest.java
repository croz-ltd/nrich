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

@SpringJUnitConfig(classes = ValidationTestConfiguration.class)
public class NotNullWhenValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldThrowExceptionWhenSpecifiedPropertyDoesntExist() {
        // given
        final NotNullWhenInvalidTestRequest request = new NotNullWhenInvalidTestRequest("property", "different property");

        // when
        final Throwable thrown = catchThrowable(() -> validator.validate(request));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotReportErrorWhenPropertyIsNullAndConditionIsFalse() {
        // given
        final NotNullWhenTestRequest request = new NotNullWhenTestRequest(null, "different property");

        // when
        final Set<ConstraintViolation<NotNullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorWhenPropertyIsNotNullAndConditionIsTrue() {
        // given
        final NotNullWhenTestRequest request = new NotNullWhenTestRequest("value", "not null");

        // when
        final Set<ConstraintViolation<NotNullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldReportErrorWhenPropertyIsNullAndConditionIsTrue() {
        // given
        final NotNullWhenTestRequest request = new NotNullWhenTestRequest(null, "not null");

        // when
        final Set<ConstraintViolation<NotNullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }
}
