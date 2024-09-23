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

package net.croz.nrich.formconfiguration.service;

import net.croz.nrich.formconfiguration.FormConfigurationTestConfiguration;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyConfiguration;
import net.croz.nrich.formconfiguration.api.model.FormConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(FormConfigurationTestConfiguration.class)
class DefaultFormConfigurationServiceTest {

    @Autowired
    private DefaultFormConfigurationService formConfigurationService;

    @Test
    void shouldFetchFormConfigurationList() {
        // when
        List<FormConfiguration> resultList = formConfigurationService.fetchFormConfigurationList();

        // then
        assertThat(resultList).hasSize(4);
        assertThat(resultList).extracting("formId").containsExactlyInAnyOrder(
            FormConfigurationTestConfiguration.SIMPLE_FORM_CONFIGURATION_FORM_ID, FormConfigurationTestConfiguration.NESTED_FORM_CONFIGURATION_FORM_ID,
            FormConfigurationTestConfiguration.NESTED_FORM_NOT_VALIDATED_CONFIGURATION_FORM_ID,
            "annotatedForm.formId"
        );
    }

    @Test
    void shouldThrowExceptionWhenNoFormConfigurationHasBeenDefinedForFormId() {
        // given
        List<String> formIdList = List.of("invalidFormId");

        // when
        Throwable thrown = catchThrowable(() -> formConfigurationService.fetchFormConfigurationList(formIdList));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldFetchSimpleFormConfiguration() {
        // given
        List<String> formIdList = List.of(FormConfigurationTestConfiguration.SIMPLE_FORM_CONFIGURATION_FORM_ID);

        // when
        List<FormConfiguration> resultList = formConfigurationService.fetchFormConfigurationList(formIdList);

        // then
        assertThat(resultList).hasSize(1);

        // and when
        FormConfiguration formConfiguration = resultList.get(0);

        // then
        assertThat(formConfiguration.formId()).isEqualTo(FormConfigurationTestConfiguration.SIMPLE_FORM_CONFIGURATION_FORM_ID);
        assertThat(formConfiguration.constrainedPropertyConfigurationList()).hasSize(4);
        assertThat(formConfiguration.constrainedPropertyConfigurationList()).extracting(ConstrainedPropertyConfiguration::path).containsExactlyInAnyOrder(
            "name", "lastName", "timestamp", "value"
        );
        assertThat(formConfiguration.constrainedPropertyConfigurationList()).extracting(ConstrainedPropertyConfiguration::javascriptType).containsExactlyInAnyOrder(
            "string", "string", "date", "number"
        );

        // and when
        ConstrainedPropertyConfiguration lastNameConstrainedPropertyConfiguration = formConfiguration.constrainedPropertyConfigurationList().stream()
            .filter(constrainedPropertyConfiguration -> "lastName".equals(constrainedPropertyConfiguration.path()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Last name not found"));

        assertThat(lastNameConstrainedPropertyConfiguration.validatorList()).hasSize(1);
        assertThat(lastNameConstrainedPropertyConfiguration.propertyType()).isEqualTo(String.class);

        formConfiguration.constrainedPropertyConfigurationList().sort(Comparator.comparing(ConstrainedPropertyConfiguration::path));

        // and when
        ConstrainedPropertyClientValidatorConfiguration lastNameValidatorConfiguration = lastNameConstrainedPropertyConfiguration.validatorList().get(0);

        // then
        assertThat(lastNameValidatorConfiguration).isNotNull();

        assertThat(lastNameValidatorConfiguration.name()).isEqualTo("Size");
        assertThat(lastNameValidatorConfiguration.argumentMap().values()).containsExactly(1, 5);
        assertThat(lastNameValidatorConfiguration.errorMessage()).isEqualTo("Size must be between: 1 and 5");
    }

    @Test
    void shouldFetchNestedFormConfiguration() {
        // given
        List<String> formIdList = List.of(FormConfigurationTestConfiguration.NESTED_FORM_CONFIGURATION_FORM_ID);

        // when
        List<FormConfiguration> resultList = formConfigurationService.fetchFormConfigurationList(formIdList);

        // then
        assertThat(resultList).hasSize(1);

        // and when
        FormConfiguration formConfiguration = resultList.get(0);

        // then
        assertThat(formConfiguration.formId()).isEqualTo(FormConfigurationTestConfiguration.NESTED_FORM_CONFIGURATION_FORM_ID);
        assertThat(formConfiguration.constrainedPropertyConfigurationList()).hasSize(5);
        assertThat(formConfiguration.constrainedPropertyConfigurationList()).extracting(ConstrainedPropertyConfiguration::path).containsExactlyInAnyOrder(
            "name", "request.name", "request.lastName", "request.timestamp", "request.value"
        );

        formConfiguration.constrainedPropertyConfigurationList().sort(Comparator.comparing(ConstrainedPropertyConfiguration::path));

        assertThat(formConfiguration.constrainedPropertyConfigurationList().get(4).validatorList()).hasSize(1);

        // and when
        ConstrainedPropertyClientValidatorConfiguration valueValidatorConfiguration = formConfiguration.constrainedPropertyConfigurationList().get(4).validatorList().get(0);

        // then
        assertThat(valueValidatorConfiguration).isNotNull();

        assertThat(valueValidatorConfiguration.name()).isEqualTo("Min");
        assertThat(valueValidatorConfiguration.argumentMap().values()).containsExactly(10L);
        assertThat(valueValidatorConfiguration.errorMessage()).isEqualTo("Minimum value is: 10");
    }

    @Test
    void shouldIgnoreNestedFieldConfigurationWhenFieldIsNotValidated() {
        // given
        List<String> formIdList = List.of(FormConfigurationTestConfiguration.NESTED_FORM_NOT_VALIDATED_CONFIGURATION_FORM_ID);

        // when
        List<FormConfiguration> resultList = formConfigurationService.fetchFormConfigurationList(formIdList);

        // then
        assertThat(resultList).hasSize(1);

        // and when
        FormConfiguration formConfiguration = resultList.get(0);

        // then
        assertThat(formConfiguration.formId()).isEqualTo(FormConfigurationTestConfiguration.NESTED_FORM_NOT_VALIDATED_CONFIGURATION_FORM_ID);
        assertThat(formConfiguration.constrainedPropertyConfigurationList()).isEmpty();
    }
}
