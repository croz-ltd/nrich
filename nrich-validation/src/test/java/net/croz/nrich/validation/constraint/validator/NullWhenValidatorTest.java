package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.NullWhenTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = ValidationTestConfiguration.class)
public class NullWhenValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldNotReportErrorWhenPropertyIsNotNullAndConditionIsFalse() {
        // given
        final NullWhenTestRequest request = new NullWhenTestRequest("value of property", "different property");

        // when
        final Set<ConstraintViolation<NullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorWhenPropertyIsNullAndConditionIsTrue() {
        // given
        final NullWhenTestRequest request = new NullWhenTestRequest(null, "not null");

        // when
        final Set<ConstraintViolation<NullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldReportErrorWhenPropertyIsNotNullAndConditionIsTrue() {
        // given
        final NullWhenTestRequest request = new NullWhenTestRequest("value", "not null");

        // when
        final Set<ConstraintViolation<NullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

}
