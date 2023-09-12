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
import net.croz.nrich.validation.constraint.stub.ValidSearchFieldsValidatorTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
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
        ValidSearchFieldsValidatorTestRequest request = new ValidSearchFieldsValidatorTestRequest(null, null, null);

        // when
        Set<ConstraintViolation<ValidSearchFieldsValidatorTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).hasSize(1);
    }

    @Test
    void shouldNotReportErrorWhenOneSearchGroupIsNotNull() {
        // given
        ValidSearchFieldsValidatorTestRequest request = new ValidSearchFieldsValidatorTestRequest("first", Instant.now(), null);

        // when
        Set<ConstraintViolation<ValidSearchFieldsValidatorTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();
    }
}
