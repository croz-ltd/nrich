package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.SpelExpressionTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(ValidationTestConfiguration.class)
public class SpelExpressionValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldNotReportErrorForNullValue() {
        // given
        SpelExpressionTestRequest request = new SpelExpressionTestRequest(null);

        // when
        Set<ConstraintViolation<SpelExpressionTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorWhenValueIsValid() {
        // given
        SpelExpressionTestRequest request = new SpelExpressionTestRequest("4adf9bf9-2656-468b-880a-706ff704e6b4");

        // when
        Set<ConstraintViolation<SpelExpressionTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldReportErrorWhenValueIsNotValid() {
        // given
        SpelExpressionTestRequest request = new SpelExpressionTestRequest("4adf9bf9-2656-xxxx-xxxx-706ff704e6b4");

        // when
        Set<ConstraintViolation<SpelExpressionTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }
}
