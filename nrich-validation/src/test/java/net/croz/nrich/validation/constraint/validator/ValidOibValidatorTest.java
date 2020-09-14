package net.croz.nrich.validation.constraint.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ValidOibValidatorTest {

    private final ValidOibValidator validOibValidator = new ValidOibValidator();

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Test
    void shouldSkipValidationOfNullValue() {
        // given
        final String oib = null;

        // when
        final boolean result = validOibValidator.isValid(oib, constraintValidatorContext);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldSkipValidationOfBlankValue() {
        // given
        final String oib = "";

        // when
        final boolean result = validOibValidator.isValid(oib, constraintValidatorContext);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseForInvalidOib() {
        // given
        final String oib = "11111111110";

        // when
        final boolean result = validOibValidator.isValid(oib, constraintValidatorContext);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForValidOib() {
        // given
        final String oib = "12655668145";

        // when
        final boolean result = validOibValidator.isValid(oib, constraintValidatorContext);

        // then
        assertThat(result).isTrue();
    }
}
