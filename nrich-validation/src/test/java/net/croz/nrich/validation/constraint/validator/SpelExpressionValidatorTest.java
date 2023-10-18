/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
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

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.SpelExpressionEnvirnomentTestRequest;
import net.croz.nrich.validation.constraint.stub.SpelExpressionSystemPropertiesTestRequest;
import net.croz.nrich.validation.constraint.stub.SpelExpressionTestRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "custom.spel.expresssion.value=valid")
@SpringJUnitConfig(ValidationTestConfiguration.class)
class SpelExpressionValidatorTest {

    @Autowired
    private Validator validator;

    static {
        System.setProperty("custom.system.property", "valid property");
    }

    @Test
    void shouldNotReportErrorForNullValue() {
        // given
        SpelExpressionTestRequest request = new SpelExpressionTestRequest(null);

        // when
        Set<ConstraintViolation<SpelExpressionTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @MethodSource("shouldNotReportErrorWhenValueIsValidMethodSource")
    @ParameterizedTest
    void shouldNotReportErrorWhenValueIsValid(Object request) {
        // when
        Set<ConstraintViolation<Object>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    private static Stream<Arguments> shouldNotReportErrorWhenValueIsValidMethodSource() {
        return Stream.of(
            Arguments.of(new SpelExpressionTestRequest("4adf9bf9-2656-468b-880a-706ff704e6b4")),
            Arguments.of(new SpelExpressionEnvirnomentTestRequest("valid")),
            Arguments.of(new SpelExpressionSystemPropertiesTestRequest("valid"))
        );
    }

    @MethodSource("shouldReportErrorWhenValueIsNotValidMethodSource")
    @ParameterizedTest
    void shouldReportErrorWhenValueIsNotValid(Object request) {
        // when
        Set<ConstraintViolation<Object>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    private static Stream<Arguments> shouldReportErrorWhenValueIsNotValidMethodSource() {
        return Stream.of(
            Arguments.of(new SpelExpressionTestRequest("4adf9bf9-2656-xxxx-xxxx-706ff704e6b4")),
            Arguments.of(new SpelExpressionEnvirnomentTestRequest("invalid")),
            Arguments.of(new SpelExpressionSystemPropertiesTestRequest("invalid"))
        );
    }
}
