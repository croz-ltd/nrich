package net.croz.nrich.registry.configuration.service.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.configuration.constants.RegistryConfigurationConstants;
import net.croz.nrich.registry.configuration.model.ColumnFieldDisplayConfiguration;
import net.croz.nrich.registry.configuration.model.FormFieldDisplayConfiguration;
import net.croz.nrich.registry.configuration.model.RegistryField;
import net.croz.nrich.registry.configuration.service.RegistryConfigurationService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RegistryConfigurationServiceImpl implements RegistryConfigurationService {

    private final EntityManager entityManager;

    private final MessageSource messageSource;

    private final List<String> readOnlyPropertyList;

    private List<RegistryField> resolveConfigurationForType(final ManagedType<?> managedType) {
        final Class<?> entityType = managedType.getJavaType();

        final List<RegistryField> registryFieldList = new ArrayList<>();

        managedType.getAttributes().forEach(attribute -> {
            final String attributeName = attribute.getName();
            final Class<?> attributeType = attribute.getJavaType();

            final boolean isOneToOne = Attribute.PersistentAttributeType.ONE_TO_ONE.equals(attribute.getPersistentAttributeType());
            final Class<?> oneToOneReferencedClass = isOneToOne ? resolveOneToOneClass(attribute) : null;

            final boolean isReadOnly = readOnlyPropertyList.contains(attributeName);

            final String formLabel = formLabel(entityType, attributeType, attributeName);
            final String columnHeader = columnHeader(entityType, attributeType, attributeName);

            final FormFieldDisplayConfiguration formFieldDisplayConfiguration = FormFieldDisplayConfiguration.builder()
                    .editable(!isReadOnly)
                    .label(formLabel)
                    .build();

            final ColumnFieldDisplayConfiguration columnFieldDisplayConfiguration = ColumnFieldDisplayConfiguration.builder()
                    .header(columnHeader)
                    .build();


            final RegistryField registryField = RegistryField.builder()
                    .name(attributeName)
                    .originalType(attributeType.getName())
                    .isOneToOne(isOneToOne)
                    .oneToOneReferencedClass(Optional.ofNullable(oneToOneReferencedClass).map(Class::getName).orElse(null))
                    .formFieldDisplayConfiguration(formFieldDisplayConfiguration)
                    .columnFieldDisplayConfiguration(columnFieldDisplayConfiguration)
                    .build();

            registryFieldList.add(registryField);
        });

        return registryFieldList;
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

}
