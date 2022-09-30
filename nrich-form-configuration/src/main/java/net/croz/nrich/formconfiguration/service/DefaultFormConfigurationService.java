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

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyConfiguration;
import net.croz.nrich.formconfiguration.api.model.FormConfiguration;
import net.croz.nrich.formconfiguration.api.service.ConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationService;
import net.croz.nrich.javascript.api.service.JavaToJavascriptTypeConversionService;
import org.springframework.cache.annotation.Cacheable;

import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultFormConfigurationService implements FormConfigurationService {

    private static final String PREFIX_FORMAT = "%s.%s";

    private final Validator validator;

    private final Map<String, Class<?>> formIdConstraintHolderMap;

    private final List<ConstrainedPropertyValidatorConverterService> constraintConverterServiceList;

    private final JavaToJavascriptTypeConversionService javaToJavascriptTypeConversionService;

    @Cacheable(value = "nrich.formConfiguration.cache", key = "'all-forms-' + T(org.springframework.context.i18n.LocaleContextHolder).locale.toLanguageTag()")
    @Override
    public List<FormConfiguration> fetchFormConfigurationList() {
        return formIdConstraintHolderMap.keySet().stream()
            .map(this::resolveFormConfiguration)
            .collect(Collectors.toList());
    }

    @Cacheable(value = "nrich.formConfiguration.cache", key = "#formIdList.hashCode() + T(org.springframework.context.i18n.LocaleContextHolder).locale.toLanguageTag()")
    @Override
    public List<FormConfiguration> fetchFormConfigurationList(List<String> formIdList) {
        return formIdList.stream()
            .map(this::resolveFormConfiguration)
            .collect(Collectors.toList());
    }

    private FormConfiguration resolveFormConfiguration(String formId) {
        Class<?> validationDefinitionHolder = Optional.ofNullable(formIdConstraintHolderMap.get(formId))
            .orElseThrow(() -> new IllegalArgumentException(String.format("Form id: %s is not registered", formId)));

        List<ConstrainedPropertyConfiguration> propertyConfigurationList = recursiveResolveFieldConfiguration(validationDefinitionHolder, new ArrayList<>(), null);

        return new FormConfiguration(formId, propertyConfigurationList);
    }

    private List<ConstrainedPropertyConfiguration> recursiveResolveFieldConfiguration(Class<?> type, List<ConstrainedPropertyConfiguration> constrainedPropertyConfigurationList, String prefix) {
        BeanDescriptor constraintBeanDescriptor = validator.getConstraintsForClass(type);
        Set<PropertyDescriptor> constraintPropertyList = constraintBeanDescriptor.getConstrainedProperties();

        Optional.ofNullable(constraintPropertyList).orElse(Collections.emptySet()).forEach(propertyDescriptor -> {
            String propertyName = propertyDescriptor.getPropertyName();
            String propertyPath = prefix == null ? propertyName : String.format(PREFIX_FORMAT, prefix, propertyName);
            List<ConstrainedPropertyClientValidatorConfiguration> constrainedPropertyClientValidatorConfigurationList = resolvePropertyValidatorList(type, propertyPath, propertyDescriptor);

            if (!constrainedPropertyClientValidatorConfigurationList.isEmpty()) {
                Class<?> propertyType = propertyDescriptor.getElementClass();
                String javascriptType = javaToJavascriptTypeConversionService.convert(propertyType);
                constrainedPropertyConfigurationList.add(
                    new ConstrainedPropertyConfiguration(propertyPath, propertyType, javascriptType, constrainedPropertyClientValidatorConfigurationList)
                );
            }

            if (shouldResolveConstraintListForType(propertyDescriptor)) {
                recursiveResolveFieldConfiguration(propertyDescriptor.getElementClass(), constrainedPropertyConfigurationList, propertyName);
            }
        });

        return constrainedPropertyConfigurationList;
    }

    private boolean shouldResolveConstraintListForType(PropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.isCascaded();
    }

    private List<ConstrainedPropertyClientValidatorConfiguration> resolvePropertyValidatorList(Class<?> parentType, String propertyPath, PropertyDescriptor propertyDescriptor) {
        Set<ConstraintDescriptor<?>> constraintDescriptorList = propertyDescriptor.getConstraintDescriptors();

        return constraintDescriptorList.stream()
            .map(constraintDescriptor -> convertProperty(constraintDescriptor, parentType, propertyPath, propertyDescriptor))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    private List<ConstrainedPropertyClientValidatorConfiguration> convertProperty(ConstraintDescriptor<?> constraintDescriptor, Class<?> parentType, String propertyPath,
                                                                                  PropertyDescriptor propertyDescriptor) {
        ConstrainedProperty constrainedProperty = ConstrainedProperty.builder()
            .constraintDescriptor(constraintDescriptor)
            .parentType(parentType)
            .path(propertyPath)
            .name(propertyDescriptor.getPropertyName())
            .type(propertyDescriptor.getElementClass())
            .build();

        ConstrainedPropertyValidatorConverterService converterService = constraintConverterServiceList.stream()
            .filter(converter -> converter.supports(constrainedProperty))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("No converter found for constrained property: %s", constrainedProperty)));

        return converterService.convert(constrainedProperty);
    }
}
