package net.croz.nrich.registry.configuration.service.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.configuration.constants.RegistryConfigurationConstants;
import net.croz.nrich.registry.configuration.model.ColumnPropertyDisplayConfiguration;
import net.croz.nrich.registry.configuration.model.FormPropertyDisplayConfiguration;
import net.croz.nrich.registry.configuration.model.JavascriptType;
import net.croz.nrich.registry.configuration.model.RegistryGroupConfiguration;
import net.croz.nrich.registry.configuration.model.RegistryProperty;
import net.croz.nrich.registry.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.configuration.util.JavaToJavascriptTypeConversionUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RegistryConfigurationServiceImpl implements RegistryConfigurationService {

    private final EntityManager entityManager;

    private final MessageSource messageSource;

    private final List<String> readOnlyPropertyList;

    @Override
    public List<RegistryGroupConfiguration> readRegistryGroupConfigurationList() {
        return null;
    }

    private List<RegistryProperty> resolveRegistryFieldListForType(final ManagedType<?> managedType) {
        final Class<?> entityType = managedType.getJavaType();

        final List<RegistryProperty> registryPropertyList = new ArrayList<>();

        managedType.getAttributes().forEach(attribute -> {
            if (shouldSkipAttribute(Collections.emptyList(), attribute)) {
                return;
            }

            final String attributeName = attribute.getName();
            final Class<?> attributeType = attribute.getJavaType();
            final JavascriptType javascriptType = JavaToJavascriptTypeConversionUtil.fromJavaType(attributeType);
            final boolean isDecimal = JavaToJavascriptTypeConversionUtil.isDecimal(attributeType);

            final boolean isOneToOne = Attribute.PersistentAttributeType.ONE_TO_ONE.equals(attribute.getPersistentAttributeType());
            final Class<?> oneToOneReferencedClass = isOneToOne ? resolveOneToOneClass(attribute) : null;

            final boolean isReadOnly = readOnlyPropertyList.contains(attributeName);

            final String formLabel = formLabel(entityType, attributeType, attributeName);
            final String columnHeader = columnHeader(entityType, attributeType, attributeName);

            final FormPropertyDisplayConfiguration formPropertyDisplayConfiguration = FormPropertyDisplayConfiguration.builder()
                    .editable(!isReadOnly)
                    .label(formLabel)
                    .build();

            final ColumnPropertyDisplayConfiguration columnPropertyDisplayConfiguration = ColumnPropertyDisplayConfiguration.builder()
                    .header(columnHeader)
                    .build();

            final RegistryProperty registryProperty = RegistryProperty.builder()
                    .name(attributeName)
                    .originalType(attributeType.getName())
                    .javascriptType(javascriptType)
                    .isDecimal(isDecimal)
                    .isOneToOne(isOneToOne)
                    .oneToOneReferencedClass(Optional.ofNullable(oneToOneReferencedClass).map(Class::getName).orElse(null))
                    .formPropertyDisplayConfiguration(formPropertyDisplayConfiguration)
                    .columnPropertyDisplayConfiguration(columnPropertyDisplayConfiguration)
                    .build();

            registryPropertyList.add(registryProperty);
        });

        return registryPropertyList;
    }

    private Class<?> resolveOneToOneClass(final Attribute<?, ?> attribute) {
        return ((ManagedType<?>) ((SingularAttribute<?, ?>) attribute).getType()).getJavaType();
    }

    private String formLabel(final Class<?> entityType, final Class<?> attributeType, final String attributeName) {
        final DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(labelMessageCodeList(entityType, attributeType, attributeName).toArray(new String[0]), attributeName);

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    private String columnHeader(final Class<?> entityType, final Class<?> attributeType, final String attributeName) {
        final List<String> headerMessageCodeList = new ArrayList<>();

        headerMessageCodeList.add(String.format(RegistryConfigurationConstants.REGISTRY_COLUMN_HEADER_FORMAT, entityType.getName(), attributeName));
        headerMessageCodeList.addAll(labelMessageCodeList(entityType, attributeType, attributeName));

        final DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(headerMessageCodeList.toArray(new String[0]), attributeName);

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    private List<String> labelMessageCodeList(final Class<?> entityType, final Class<?> attributeType, final String attributeName) {
        return Arrays.asList(
                String.format(RegistryConfigurationConstants.REGISTRY_DISPLAY_LABEL_FORMAT, entityType.getName(), attributeName),
                String.format(RegistryConfigurationConstants.REGISTRY_DISPLAY_LABEL_FORMAT, attributeName, attributeType.getName()),
                String.format(RegistryConfigurationConstants.REGISTRY_DISPLAY_LABEL_SHORT_FORMAT, attributeType.getName())
        );
    }

    private boolean shouldSkipAttribute(final List<String> entityIgnoredPropertyList, final Attribute<?, ?> attribute) {
        return attribute.isCollection() || entityIgnoredPropertyList.contains(attribute.getName());
    }
}
