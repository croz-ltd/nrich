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

package net.croz.nrich.formconfiguration.testutil;

import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.api.model.FormConfiguration;
import net.croz.nrich.formconfiguration.api.request.FetchFormConfigurationRequest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class FormConfigurationGeneratingUtil {

    private FormConfigurationGeneratingUtil() {
    }

    public static ConstrainedProperty createConstrainedProperty(Class<?> parentType) {
        return createConstrainedProperty(parentType, Collections.emptyMap(), "propertyName");
    }

    public static ConstrainedProperty createConstrainedProperty(Class<?> parentType, Map<String, Object> attributeMap) {
        return createConstrainedProperty(parentType, attributeMap, "propertyName");
    }

    public static ConstrainedProperty createConstrainedProperty(Class<?> parentType, Map<String, Object> attributeMap, String propertyName) {
        @SuppressWarnings("unchecked")
        ConstraintDescriptor<Annotation> constraintDescriptor = mock(ConstraintDescriptor.class);
        Annotation annotation = mock(Annotation.class);

        doReturn(NotNull.class).when(annotation).annotationType();

        when(constraintDescriptor.getAttributes()).thenReturn(attributeMap);
        when(constraintDescriptor.getAnnotation()).thenReturn(annotation);

        return ConstrainedProperty.builder()
            .constraintDescriptor(constraintDescriptor)
            .name(propertyName)
            .path("propertyPath")
            .type(String.class)
            .parentType(parentType).build();

    }

    public static FetchFormConfigurationRequest createFetchFormConfigurationRequest() {
        FetchFormConfigurationRequest request = new FetchFormConfigurationRequest();

        request.setFormIdList(Arrays.asList("formId1", "formId2"));

        return request;
    }

    public static FormConfiguration createFormConfiguration() {
        return new FormConfiguration("formId1", Collections.emptyList());
    }
}
