package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.LastTimestampInDayTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(ValidationTestConfiguration.class)
class LastTimestampInDayValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldNotReportErrorForNullValue() {
        // given
        LastTimestampInDayTestRequest request = new LastTimestampInDayTestRequest(null);

        // when
        Set<ConstraintViolation<LastTimestampInDayTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorDateIsBeforeLastTimestampInDay() {
        // given
        LastTimestampInDayTestRequest request = new LastTimestampInDayTestRequest(LocalDate.now());

        // when
        Set<ConstraintViolation<LastTimestampInDayTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldReportErrorWhenDateIsAfterLastTimestampInDay() {
        // given
        LastTimestampInDayTestRequest request = new LastTimestampInDayTestRequest(LocalDate.now().plusDays(1));

        // when
        Set<ConstraintViolation<LastTimestampInDayTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

}
