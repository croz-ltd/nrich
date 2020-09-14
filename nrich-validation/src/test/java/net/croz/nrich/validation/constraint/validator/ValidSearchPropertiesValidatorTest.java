package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.ValidSearchFieldsValidatorTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(ValidationTestConfiguration.class)
class ValidSearchPropertiesValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldReportErrorWhenBothSearchFieldGroupsAreNull() {
        // given
        final ValidSearchFieldsValidatorTestRequest request = new ValidSearchFieldsValidatorTestRequest(null, null, null);

        // when
        final Set<ConstraintViolation<ValidSearchFieldsValidatorTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).hasSize(1);
    }

    @Test
    void shouldNotReportErrorWhenOneSearchGroupIsNotNull() {
        // given
        final ValidSearchFieldsValidatorTestRequest request = new ValidSearchFieldsValidatorTestRequest("first", Instant.now(), null);

        // when
        final Set<ConstraintViolation<ValidSearchFieldsValidatorTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

}
