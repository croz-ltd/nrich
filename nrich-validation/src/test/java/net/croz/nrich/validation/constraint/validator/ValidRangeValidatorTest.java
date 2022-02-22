package net.croz.nrich.validation.constraint.validator;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.ValidRangeValidatorDifferentTypeTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidRangeValidatorInclusiveTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidRangeValidatorNotComparableTestRequest;
import net.croz.nrich.validation.constraint.stub.ValidRangeValidatorTestRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringJUnitConfig(ValidationTestConfiguration.class)
class ValidRangeValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldThrowExceptionWhenFieldAreNotComparable() {
        // given
        ValidRangeValidatorDifferentTypeTestRequest request = new ValidRangeValidatorDifferentTypeTestRequest(1L, Instant.now());

        // when
        Throwable thrown = catchThrowable(() -> validator.validate(request));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenFieldAreOfDifferentType() {
        // given
        ValidRangeValidatorNotComparableTestRequest request = new ValidRangeValidatorNotComparableTestRequest(new ValidRangeValidatorNotComparableTestRequest.NotComparable(), new ValidRangeValidatorNotComparableTestRequest.NotComparable());

        // when
        Throwable thrown = catchThrowable(() -> validator.validate(request));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotReturnErrorWhenValuesAreEqualAndInclusiveFlagIsEnabled() {
        // given
        ValidRangeValidatorInclusiveTestRequest request = new ValidRangeValidatorInclusiveTestRequest(1, 1);

        // when
        Set<ConstraintViolation<ValidRangeValidatorInclusiveTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @MethodSource("shouldReturnIsValidForRangeValuesMethodSource")
    @ParameterizedTest
    void shouldReturnIsValidForRangeValues(Instant firstValue, Instant secondValue, boolean isValid) {
        // given
        ValidRangeValidatorTestRequest request = new ValidRangeValidatorTestRequest(firstValue, secondValue);

        // when
        Set<ConstraintViolation<ValidRangeValidatorTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).hasSize(isValid ? 0 : 1);
    }

    private static Stream<Arguments> shouldReturnIsValidForRangeValuesMethodSource() {
        Instant now = Instant.now();

        return Stream.of(
                arguments(null, null, true),
                arguments(now, null, true),
                arguments(null, now, true),
                arguments(now, now.plus(1, ChronoUnit.DAYS), true),
                arguments(now.plus(1, ChronoUnit.DAYS), now, false),
                arguments(now, now, false)
        );
    }
}
