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

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.stub.NullWhenTestRequest;
import net.croz.nrich.validation.constraint.stub.NullWhenTestRequestWithAutowiredService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(ValidationTestConfiguration.class)
class NullWhenValidatorTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldNotReportErrorWhenPropertyIsNotNullAndConditionIsFalse() {
        // given
        NullWhenTestRequest request = new NullWhenTestRequest("value of property", "different property");

        // when
        Set<ConstraintViolation<NullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorWhenPropertyIsNullAndConditionIsTrue() {
        // given
        NullWhenTestRequest request = new NullWhenTestRequest(null, "not null");

        // when
        Set<ConstraintViolation<NullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldReportErrorWhenPropertyIsNotNullAndConditionIsTrue() {
        // given
        NullWhenTestRequest request = new NullWhenTestRequest("value", "not null");

        // when
        Set<ConstraintViolation<NullWhenTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldUseAutowiredServiceAndReportError() {
        // given
        NullWhenTestRequestWithAutowiredService request = new NullWhenTestRequestWithAutowiredService("value", "not null");

        // when
        Set<ConstraintViolation<NullWhenTestRequestWithAutowiredService>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }
}
