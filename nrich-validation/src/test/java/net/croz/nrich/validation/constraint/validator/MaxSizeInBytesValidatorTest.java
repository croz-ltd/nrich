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
