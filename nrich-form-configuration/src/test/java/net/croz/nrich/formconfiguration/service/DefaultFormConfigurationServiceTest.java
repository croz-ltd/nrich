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

package net.croz.nrich.formconfiguration.service;

import net.croz.nrich.formconfiguration.FormConfigurationTestConfiguration;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyConfiguration;
import net.croz.nrich.formconfiguration.api.model.FormConfiguration;
import net.croz.nrich.javascript.model.JavascriptType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(FormConfigurationTestConfiguration.class)
class DefaultFormConfigurationServiceTest {

    @Autowired
    private DefaultFormConfigurationService formConfigurationService;

    @Test
    void shouldThrowExceptionWhenNoFormConfigurationHasBeenDefinedForFormId() {
        // given
        List<String> formIdList = Collections.singletonList("invalidFormId");

        // when
        Throwable thrown = catchThrowable(() -> formConfigurationService.fetchFormConfigurationList(formIdList));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldResolveSimpleFormFieldConfiguration() {
        // given
        List<String> formIdList = Collections.singletonList(FormConfigurationTestConfiguration.SIMPLE_FORM_CONFIGURATION_FORM_ID);

        // when
        List<FormConfiguration> resultList = formConfigurationService.fetchFormConfigurationList(formIdList);

        // then
        assertThat(resultList).hasSize(1);

        // and when
        FormConfiguration formConfiguration = resultList.get(0);

        // then
        assertThat(formConfiguration.getFormId()).isEqualTo(FormConfigurationTestConfiguration.SIMPLE_FORM_CONFIGURATION_FORM_ID);
        assertThat(formConfiguration.getConstrainedPropertyConfigurationList()).hasSize(4);
        assertThat(formConfiguration.getConstrainedPropertyConfigurationList()).extracting(ConstrainedPropertyConfiguration::getPath).containsExactlyInAnyOrder(
            "name", "lastName", "timestamp", "value"
        );
        assertThat(formConfiguration.getConstrainedPropertyConfigurationList()).extracting(ConstrainedPropertyConfiguration::getJavascriptType).containsExactlyInAnyOrder(
            JavascriptType.STRING, JavascriptType.STRING, JavascriptType.DATE, JavascriptType.NUMBER
        );

        assertThat(formConfiguration.getConstrainedPropertyConfigurationList().get(0).getValidatorList()).hasSize(1);
        assertThat(formConfiguration.getConstrainedPropertyConfigurationList().get(0).getPropertyType()).isEqualTo(String.class);

        formConfiguration.getConstrainedPropertyConfigurationList().sort(Comparator.comparing(ConstrainedPropertyConfiguration::getPath));

        // and when
        ConstrainedPropertyClientValidatorConfiguration lastNameValidatorConfiguration = formConfiguration.getConstrainedPropertyConfigurationList().get(0).getValidatorList().get(0);

        // then
        assertThat(lastNameValidatorConfiguration).isNotNull();

        assertThat(lastNameValidatorConfiguration.getName()).isEqualTo("Size");
        assertThat(lastNameValidatorConfiguration.getArgumentMap().values()).containsExactly(1, 5);
        assertThat(lastNameValidatorConfiguration.getErrorMessage()).isEqualTo("Size must be between: 1 and 5");
    }

    @Test
    void shouldResolveNestedFormConfiguration() {
        // given
        List<String> formIdList = Collections.singletonList(FormConfigurationTestConfiguration.NESTED_FORM_CONFIGURATION_FORM_ID);

        // when
        List<FormConfiguration> resultList = formConfigurationService.fetchFormConfigurationList(formIdList);

        // then
        assertThat(resultList).hasSize(1);

        // and when
        FormConfiguration formConfiguration = resultList.get(0);

        // then
        assertThat(formConfiguration.getFormId()).isEqualTo(FormConfigurationTestConfiguration.NESTED_FORM_CONFIGURATION_FORM_ID);
        assertThat(formConfiguration.getConstrainedPropertyConfigurationList()).hasSize(5);
        assertThat(formConfiguration.getConstrainedPropertyConfigurationList()).extracting(ConstrainedPropertyConfiguration::getPath).containsExactlyInAnyOrder(
            "name", "request.name", "request.lastName", "request.timestamp", "request.value"
        );

        formConfiguration.getConstrainedPropertyConfigurationList().sort(Comparator.comparing(ConstrainedPropertyConfiguration::getPath));

        assertThat(formConfiguration.getConstrainedPropertyConfigurationList().get(4).getValidatorList()).hasSize(1);

        // and when
        ConstrainedPropertyClientValidatorConfiguration valueValidatorConfiguration = formConfiguration.getConstrainedPropertyConfigurationList().get(4).getValidatorList().get(0);

        // then
        assertThat(valueValidatorConfiguration).isNotNull();

        assertThat(valueValidatorConfiguration.getName()).isEqualTo("Min");
        assertThat(valueValidatorConfiguration.getArgumentMap().values()).containsExactly(10L);
        assertThat(valueValidatorConfiguration.getErrorMessage()).isEqualTo("Minimum value is: 10");
    }

    @Test
    void shouldIgnoreNestedFieldConfigurationWhenFieldIsNotValidated() {
        // given
        List<String> formIdList = Collections.singletonList(FormConfigurationTestConfiguration.NESTED_FORM_NOT_VALIDATED_CONFIGURATION_FORM_ID);

        // when
        List<FormConfiguration> resultList = formConfigurationService.fetchFormConfigurationList(formIdList);

        // then
        assertThat(resultList).hasSize(1);

        // and when
        FormConfiguration formConfiguration = resultList.get(0);

        // then
        assertThat(formConfiguration.getFormId()).isEqualTo(FormConfigurationTestConfiguration.NESTED_FORM_NOT_VALIDATED_CONFIGURATION_FORM_ID);
        assertThat(formConfiguration.getConstrainedPropertyConfigurationList()).isEmpty();
    }
}
