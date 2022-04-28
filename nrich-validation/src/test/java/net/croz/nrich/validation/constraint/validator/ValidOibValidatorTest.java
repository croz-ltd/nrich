/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
