package net.croz.nrich.formconfiguration.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyConfiguration;
import net.croz.nrich.formconfiguration.api.model.FormConfiguration;
import net.croz.nrich.formconfiguration.api.request.FetchFormConfigurationRequest;
import net.croz.nrich.formconfiguration.api.service.ConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationService;
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

    @Cacheable(value = "nrich.formConfiguration.cache", key = "#request.hashCode() + T(org.springframework.context.i18n.LocaleContextHolder).locale.toLanguageTag()")
    @Override
    public List<FormConfiguration> fetchFormConfigurationList(final FetchFormConfigurationRequest request) {
        return request.getFormIdList().stream()
                .map(this::resolveFormConfiguration)
                .collect(Collectors.toList());
    }

    private FormConfiguration resolveFormConfiguration(final String formId) {
        final Class<?> validationDefinitionHolder = Optional.ofNullable(formIdConstraintHolderMap.get(formId)).orElseThrow(() -> new IllegalArgumentException(String.format("Form id: %s is not registered", formId)));

        final List<ConstrainedPropertyConfiguration> constrainedPropertyConfigurationList = new ArrayList<>();

        recursiveResolveFieldConfiguration(validationDefinitionHolder, constrainedPropertyConfigurationList, null);

        return new FormConfiguration(formId, constrainedPropertyConfigurationList);
    }

    private List<ConstrainedPropertyConfiguration> recursiveResolveFieldConfiguration(final Class<?> type, final List<ConstrainedPropertyConfiguration> constrainedPropertyConfigurationList, final String prefix) {
        final BeanDescriptor constraintBeanDescriptor = validator.getConstraintsForClass(type);
        final Set<PropertyDescriptor> constraintPropertyList = constraintBeanDescriptor.getConstrainedProperties();

        Optional.ofNullable(constraintPropertyList).orElse(Collections.emptySet()).forEach(propertyDescriptor -> {
            final String propertyName = propertyDescriptor.getPropertyName();
            final String propertyPath = prefix == null ? propertyName : String.format(PREFIX_FORMAT, prefix, propertyName);
            final List<ConstrainedPropertyClientValidatorConfiguration> constrainedPropertyClientValidatorConfigurationList = resolvePropertyValidatorList(type, propertyPath, propertyDescriptor);

            if (!constrainedPropertyClientValidatorConfigurationList.isEmpty()) {
                constrainedPropertyConfigurationList.add(new ConstrainedPropertyConfiguration(propertyPath, constrainedPropertyClientValidatorConfigurationList));
            }

            if (shouldResolveConstraintListForType(propertyDescriptor)) {
                recursiveResolveFieldConfiguration(propertyDescriptor.getElementClass(), constrainedPropertyConfigurationList, propertyName);
            }
        });

        return constrainedPropertyConfigurationList;
    }

    private boolean shouldResolveConstraintListForType(final PropertyDescriptor propertyDescriptor) {
        return propertyDescriptor.isCascaded();
    }

    private List<ConstrainedPropertyClientValidatorConfiguration> resolvePropertyValidatorList(final Class<?> parentType, final String propertyPath, final PropertyDescriptor propertyDescriptor) {
        final Set<ConstraintDescriptor<?>> constraintDescriptorList = propertyDescriptor.getConstraintDescriptors();

        return constraintDescriptorList.stream()
                .map(constraintDescriptor -> convertProperty(constraintDescriptor, parentType, propertyPath, propertyDescriptor))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<ConstrainedPropertyClientValidatorConfiguration> convertProperty(final ConstraintDescriptor<?> constraintDescriptor, final Class<?> parentType, final String propertyPath, final PropertyDescriptor propertyDescriptor) {
        final ConstrainedProperty constrainedProperty = ConstrainedProperty.builder()
                .constraintDescriptor(constraintDescriptor)
                .parentType(parentType)
                .path(propertyPath)
                .name(propertyDescriptor.getPropertyName())
                .type(propertyDescriptor.getElementClass())
                .build();

        final ConstrainedPropertyValidatorConverterService converterService = constraintConverterServiceList.stream()
                .filter(converter -> converter.supports(constrainedProperty))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("No converter found for constrained property: %s", constrainedProperty)));

        return converterService.convert(constrainedProperty);
    }
}
