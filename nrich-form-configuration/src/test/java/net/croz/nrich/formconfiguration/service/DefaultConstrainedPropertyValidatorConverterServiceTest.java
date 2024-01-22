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
import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration;
import net.croz.nrich.formconfiguration.stub.FormConfigurationServiceTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static net.croz.nrich.formconfiguration.testutil.FormConfigurationGeneratingUtil.createConstrainedProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringJUnitConfig(FormConfigurationTestConfiguration.class)
class DefaultConstrainedPropertyValidatorConverterServiceTest {

    @Autowired
    private DefaultConstrainedPropertyValidatorConverterService defaultConstrainedPropertyValidatorConverterService;

    @Test
    void shouldConvertConstrainedPropertyToValidatorConfiguration() {
        // given
        ConstrainedProperty constrainedProperty = createConstrainedProperty(FormConfigurationServiceTestRequest.class);

        // when
        List<ConstrainedPropertyClientValidatorConfiguration> validationConfigurationList = defaultConstrainedPropertyValidatorConverterService.convert(constrainedProperty);

        // then
        assertThat(validationConfigurationList).hasSize(1);

        // and when
        ConstrainedPropertyClientValidatorConfiguration validatorConfiguration = validationConfigurationList.get(0);

        // then
        assertThat(validatorConfiguration).isNotNull();
        assertThat(validatorConfiguration.argumentMap()).isEmpty();
        assertThat(validatorConfiguration.name()).isEqualTo("NotNull");
        assertThat(validatorConfiguration.errorMessage()).isEqualTo("Really cannot be null");
    }

    @Test
    void shouldSupportAll() {
        // given
        ConstrainedProperty property = mock(ConstrainedProperty.class);

        // when
        boolean result = defaultConstrainedPropertyValidatorConverterService.supports(property);

        // then
        assertThat(result).isTrue();
    }
}
