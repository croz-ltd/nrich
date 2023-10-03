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

package net.croz.nrich.validation.constraint.support.disableconstraints;

import net.croz.nrich.validation.ValidationTestConfiguration;
import net.croz.nrich.validation.constraint.support.disableconstraints.stub.DisableConstraintsParentTestRequest;
import net.croz.nrich.validation.constraint.support.disableconstraints.stub.DisableConstraintsPropertyAnnotationTestRequest;
import net.croz.nrich.validation.constraint.support.disableconstraints.stub.DisableConstraintsTypeAnnotationTestRequest;
import org.hibernate.validator.HibernateValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(ValidationTestConfiguration.class)
class ValidatorAdapterTest {

    @Autowired
    private Validator validator;

    @Test
    void shouldNotReportErrorForPropertyDisabledConstraints() {
        // given
        DisableConstraintsPropertyAnnotationTestRequest request = new DisableConstraintsPropertyAnnotationTestRequest(null, null);

        // when
        Set<ConstraintViolation<DisableConstraintsPropertyAnnotationTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();

        // and when
        constraintViolationList = validator.validateProperty(request, "age");

        // then
        assertThat(constraintViolationList).isEmpty();

        // and when
        constraintViolationList = validator.validateValue(DisableConstraintsPropertyAnnotationTestRequest.class, "age", null);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldNotReportErrorForTypeDisabledConstraints() {
        // given
        DisableConstraintsTypeAnnotationTestRequest request = new DisableConstraintsTypeAnnotationTestRequest(null, null);

        // when
        Set<ConstraintViolation<DisableConstraintsTypeAnnotationTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isEmpty();

        // and when
        constraintViolationList = validator.validateProperty(request, "age");

        // then
        assertThat(constraintViolationList).isEmpty();

        // and when
        constraintViolationList = validator.validateValue(DisableConstraintsTypeAnnotationTestRequest.class, "age", null);

        // then
        assertThat(constraintViolationList).isEmpty();
    }

    @Test
    void shouldReportErrorsForParentValidation() {
        // given
        DisableConstraintsParentTestRequest request = new DisableConstraintsParentTestRequest(null, null);

        // when
        Set<ConstraintViolation<DisableConstraintsParentTestRequest>> constraintViolationList = validator.validate(request);

        // then
        assertThat(constraintViolationList).isNotEmpty();
    }

    @Test
    void shouldNotReturnDisabledConstraintsOnProperty() {
        // when
        BeanDescriptor result = validator.getConstraintsForClass(DisableConstraintsPropertyAnnotationTestRequest.class);

        // then
        assertThat(result.getConstraintsForProperty("name").getConstraintDescriptors()).isEmpty();
        assertThat(result.getConstraintsForProperty("age").getConstraintDescriptors()).isEmpty();
    }

    @Test
    void shouldNotReturnDisabledConstraintsOnType() {
        // when
        BeanDescriptor result = validator.getConstraintsForClass(DisableConstraintsTypeAnnotationTestRequest.class);

        // then
        assertThat(result.getConstraintsForProperty("name").getConstraintDescriptors()).isEmpty();
        assertThat(result.getConstraintsForProperty("name").findConstraints().getConstraintDescriptors()).isEmpty();
        assertThat(result.getConstraintsForProperty("age").getConstraintDescriptors()).isEmpty();
        assertThat(result.getConstraintsForProperty("age").findConstraints().getConstraintDescriptors()).isEmpty();
    }

    @Test
    void shouldReturnConstraintsOnType() {
        // when
        BeanDescriptor result = validator.getConstraintsForClass(DisableConstraintsParentTestRequest.class);

        // then
        assertThat(result.getConstraintsForProperty("name").getConstraintDescriptors()).isNotEmpty();
        assertThat(result.getConstraintsForProperty("name").findConstraints().getConstraintDescriptors()).isNotEmpty();
        assertThat(result.getConstraintsForProperty("age").getConstraintDescriptors()).isNotEmpty();
        assertThat(result.getConstraintsForProperty("age").findConstraints().getConstraintDescriptors()).isNotEmpty();
    }

    @Test
    void shouldUnwrap() {
        // when
        Object result = validator.unwrap(HibernateValidatorFactory.class);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void shouldReturnForExecutableValidator() {
        // when
        ExecutableValidator result = validator.forExecutables();

        // then
        assertThat(result).isNotNull();
    }
}
