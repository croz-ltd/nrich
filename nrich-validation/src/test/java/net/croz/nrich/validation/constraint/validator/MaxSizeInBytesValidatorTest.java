package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.MaxSizeInBytesTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(ValidationTestConfiguration.class)
class MaxSizeInBytesValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldNotReportErrorForNullValue() {
        // given
        MaxSizeInBytesTestRequest request = new MaxSizeInBytesTestRequest(null);

        // when
        Set<ConstraintViolation<MaxSizeInBytesTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorWhenSizeInBytesIsLessThanMax() {
        // given
        MaxSizeInBytesTestRequest request = new MaxSizeInBytesTestRequest("size");

        // when
        Set<ConstraintViolation<MaxSizeInBytesTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldReportErrorWhenSizeIsGreaterThanMax() {
        // given
        MaxSizeInBytesTestRequest request = new MaxSizeInBytesTestRequest("size greater than max allowed");

        // when
        Set<ConstraintViolation<MaxSizeInBytesTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }
}
