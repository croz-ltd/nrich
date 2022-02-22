package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.NullWhenTestRequest;
import net.croz.nrich.validation.constraint.stub.NullWhenTestRequestWithAutowiredService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(ValidationTestConfiguration.class)
class NullWhenValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldNotReportErrorWhenPropertyIsNotNullAndConditionIsFalse() {
        // given
        NullWhenTestRequest request = new NullWhenTestRequest("value of property", "different property");

        // when
        Set<ConstraintViolation<NullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorWhenPropertyIsNullAndConditionIsTrue() {
        // given
        NullWhenTestRequest request = new NullWhenTestRequest(null, "not null");

        // when
        Set<ConstraintViolation<NullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldReportErrorWhenPropertyIsNotNullAndConditionIsTrue() {
        // given
        NullWhenTestRequest request = new NullWhenTestRequest("value", "not null");

        // when
        Set<ConstraintViolation<NullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldUseAutowiredServiceAndReportError() {
        // given
        NullWhenTestRequestWithAutowiredService request = new NullWhenTestRequestWithAutowiredService("value", "not null");

        // when
        Set<ConstraintViolation<NullWhenTestRequestWithAutowiredService>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }
}
