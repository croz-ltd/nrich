package net.croz.nrich.validation.constraint.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ValidOibValidatorTest {

    private final ValidOibValidator validOibValidator = new ValidOibValidator();

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @NullAndEmptySource
    @ParameterizedTest
    void shouldSkipValidationOfNullOrEmptyValue(String oib) {
        // when
        boolean result = validOibValidator.isValid(oib, constraintValidatorContext);

        // then
        assertThat(result).isTrue();
    }

    @ValueSource(strings = { "invalidOib", "11111111110" })
    @ParameterizedTest
    void shouldReturnFalseForInvalidOib(String oib) {
        // when
        boolean result = validOibValidator.isValid(oib, constraintValidatorContext);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrueForValidOib() {
        // given
        String oib = "12655668145";

        // when
        boolean result = validOibValidator.isValid(oib, constraintValidatorContext);

        // then
        assertThat(result).isTrue();
    }
}
