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
import net.croz.nrich.validation.constraint.stub.NotNullWhenInvalidTestRequest;
import net.croz.nrich.validation.constraint.stub.NotNullWhenTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(ValidationTestConfiguration.class)
class NotNullWhenValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldThrowExceptionWhenSpecifiedPropertyDoesntExist() {
        // given
        NotNullWhenInvalidTestRequest request = new NotNullWhenInvalidTestRequest("property", "different property");

        // when
        Throwable thrown = catchThrowable(() -> validator.validate(request));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldNotReportErrorWhenPropertyIsNullAndConditionIsFalse() {
        // given
        NotNullWhenTestRequest request = new NotNullWhenTestRequest(null, "different property");

        // when
        Set<ConstraintViolation<NotNullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorWhenPropertyIsNotNullAndConditionIsTrue() {
        // given
        NotNullWhenTestRequest request = new NotNullWhenTestRequest("value", "not null");

        // when
        Set<ConstraintViolation<NotNullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldReportErrorWhenPropertyIsNullAndConditionIsTrue() {
        // given
        NotNullWhenTestRequest request = new NotNullWhenTestRequest(null, "not null");

        // when
        Set<ConstraintViolation<NotNullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }
}
