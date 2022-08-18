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

import net.croz.nrich.formconfiguration.stub.FormConfigurationAnnotatedTestRequest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class DefaultFormConfigurationAnnotationResolvingServiceTest {

    private final DefaultFormConfigurationAnnotationResolvingService defaultFormConfigurationAnnotationResolvingService = new DefaultFormConfigurationAnnotationResolvingService();

    @Test
    void shouldNotFailOnNullPackageList() {
        // when
        Throwable thrown = catchThrowable(() -> defaultFormConfigurationAnnotationResolvingService.resolveFormConfigurations(null));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldResolveAnnotatedFormConfigurationClasses() {
        // given
        List<String> packageList = Collections.singletonList("net.croz");

        // when
        Map<String, Class<?>> result = defaultFormConfigurationAnnotationResolvingService.resolveFormConfigurations(packageList);

        // then
        assertThat(result).containsEntry("annotatedForm.formId", FormConfigurationAnnotatedTestRequest.class);
    }
}
