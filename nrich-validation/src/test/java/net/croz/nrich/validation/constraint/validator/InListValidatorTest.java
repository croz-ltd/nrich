package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.InListTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(ValidationTestConfiguration.class)
class InListValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldNotReportErrorForNullValue() {
        // given
        InListTestRequest request = new InListTestRequest(null);

        // when
        Set<ConstraintViolation<InListTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorWhenValueIsInList() {
        // given
        InListTestRequest request = new InListTestRequest("in list");

        // when
        Set<ConstraintViolation<InListTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldReportErrorWhenValueIsNotInList() {
        InListTestRequest request = new InListTestRequest("not in list");

        // when
        Set<ConstraintViolation<InListTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }
}
