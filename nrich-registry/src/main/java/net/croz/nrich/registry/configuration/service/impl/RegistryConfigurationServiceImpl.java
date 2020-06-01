package net.croz.nrich.registry.configuration.service.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.configuration.comparator.RegistryGroupConfigurationComparator;
import net.croz.nrich.registry.configuration.constants.RegistryConfigurationConstants;
import net.croz.nrich.registry.configuration.model.JavascriptType;
import net.croz.nrich.registry.configuration.model.RegistryConfiguration;
import net.croz.nrich.registry.configuration.model.RegistryGroupConfiguration;
import net.croz.nrich.registry.configuration.model.RegistryGroupDefinitionHolder;
import net.croz.nrich.registry.configuration.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.configuration.model.RegistryProperty;
import net.croz.nrich.registry.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.configuration.util.JavaToJavascriptTypeConversionUtil;
import net.croz.nrich.registry.core.model.ManagedTypeWrapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RegistryConfigurationServiceImpl implements RegistryConfigurationService {

    private final EntityManager entityManager;

    private final MessageSource messageSource;

    private final List<String> readOnlyPropertyList;

    private final RegistryGroupDefinitionHolder registryGroupDefinitionHolder;

    private final Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap;

    @Override
    public List<RegistryGroupConfiguration> readRegistryGroupConfigurationList() {
        final Set<ManagedType<?>> managedTypeList = entityManager.getMetamodel().getManagedTypes();

        final List<RegistryGroupConfiguration> registryGroupConfigurationList = new ArrayList<>();

        registryGroupDefinitionHolder.getRegistryGroupDefinitionList().forEach(registryGroupDefinition -> {
            final List<ManagedType<?>> includedManagedTypeList = managedTypeList.stream()
                    .filter(managedType -> includeManagedType(managedType, registryGroupDefinition.getIncludeEntityPatternList(), registryGroupDefinition.getExcludeEntityPatternList()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(includedManagedTypeList)) {
                return;
            }

            final String registryGroupIdDisplay = groupDisplayLabel(registryGroupDefinition.getRegistryGroupId());

            final List<RegistryConfiguration> registryConfigurationList = includedManagedTypeList.stream()
                    .map(managedType -> createRegistryConfiguration(registryGroupDefinition.getRegistryGroupId(), managedType))
                    .collect(Collectors.toList());

            final RegistryGroupConfiguration registryConfiguration = new RegistryGroupConfiguration(registryGroupDefinition.getRegistryGroupId(), registryGroupIdDisplay, registryConfigurationList);

            registryGroupConfigurationList.add(registryConfiguration);

        });

        registryGroupConfigurationList.sort(new RegistryGroupConfigurationComparator(registryGroupDefinitionHolder.getRegistryGroupDisplayOrderList()));

        return registryGroupConfigurationList;
    }

    private RegistryConfiguration createRegistryConfiguration(final String registryGroupId, final ManagedType<?> managedType) {
        final Class<?> entityType = managedType.getJavaType();
        final RegistryOverrideConfiguration registryOverrideConfiguration = registryOverrideConfigurationMap.get(entityType);
        final ManagedTypeWrapper managedTypeWrapper = new ManagedTypeWrapper(managedType);

        final String registryDisplayName = registryDisplayLabel(entityType);
        final boolean isHistoryAvailable = Optional.ofNullable(registryOverrideConfiguration).map(RegistryOverrideConfiguration::isHistoryAvailable).orElse(isAudited(entityType));
        final List<RegistryProperty> registryPropertyList = resolveRegistryPropertyListForType(managedType, registryOverrideConfiguration);

        return RegistryConfiguration.builder()
                .category(registryGroupId)
                .registryId(entityType.getName())
                .registryDisplayName(registryDisplayName)
                .registryPropertyList(registryPropertyList)
                .readOnly(Optional.ofNullable(registryOverrideConfiguration).map(RegistryOverrideConfiguration::isReadOnly).orElse(false))
                .deletable(Optional.ofNullable(registryOverrideConfiguration).map(RegistryOverrideConfiguration::isDeletable).orElse(true))
                .isHistoryAvailable(isHistoryAvailable)
                .isIdentifierAssigned(managedTypeWrapper.isIdentifierAssigned())
                .isCompositeIdentity(managedTypeWrapper.isCompositeIdentity())
                .compositeIdentityPropertyNameList(managedTypeWrapper.compositeIdentityPropertyNameList())
                .build();
    }

    private List<RegistryProperty> resolveRegistryPropertyListForType(final ManagedType<?> managedType, final RegistryOverrideConfiguration registryOverrideConfiguration) {
        final Class<?> entityType = managedType.getJavaType();
        final List<String> ignoredPropertyList = Optional.ofNullable(registryOverrideConfiguration).map(RegistryOverrideConfiguration::getIgnoredPropertyList).orElse(Collections.emptyList());
        final List<String> readOnlyOverridePropertyList = Optional.ofNullable(registryOverrideConfiguration).map(RegistryOverrideConfiguration::getNonEditablePropertyList).orElse(Collections.emptyList());
        final List<String> nonSortablePropertyList = Optional.ofNullable(registryOverrideConfiguration).map(RegistryOverrideConfiguration::getNonSortablePropertyList).orElse(Collections.emptyList());

        final List<RegistryProperty> registryPropertyList = new ArrayList<>();

        managedType.getAttributes().forEach(attribute -> {
            if (shouldSkipAttribute(ignoredPropertyList, attribute)) {
                return;
            }

            final String attributeName = attribute.getName();
            final Class<?> attributeType = attribute.getJavaType();
            final JavascriptType javascriptType = JavaToJavascriptTypeConversionUtil.fromJavaType(attributeType);
            final boolean isDecimal = JavaToJavascriptTypeConversionUtil.isDecimal(attributeType);

            final boolean isOneToOne = Attribute.PersistentAttributeType.ONE_TO_ONE.equals(attribute.getPersistentAttributeType());
            final Class<?> oneToOneReferencedClass = isOneToOne ? resolveOneToOneClass(attribute) : null;

            final boolean isReadOnly = readOnlyPropertyList.contains(attributeName) || readOnlyOverridePropertyList.contains(attributeName);
            final boolean isSortable = !nonSortablePropertyList.contains(attributeName);

            final String formLabel = formLabel(entityType, attributeType, attributeName);
            final String columnHeader = columnHeader(entityType, attributeType, attributeName);

            final RegistryProperty registryProperty = RegistryProperty.builder()
                    .name(attributeName)
                    .originalType(attributeType.getName())
                    .javascriptType(javascriptType)
                    .isDecimal(isDecimal)
                    .isOneToOne(isOneToOne)
                    .oneToOneReferencedClass(Optional.ofNullable(oneToOneReferencedClass).map(Class::getName).orElse(null))
                    .formLabel(formLabel)
                    .columnHeader(columnHeader)
                    .editable(!isReadOnly)
                    .sortable(isSortable)
                    .build();

            registryPropertyList.add(registryProperty);

        });

        return registryPropertyList;
    }

    private Class<?> resolveOneToOneClass(final Attribute<?, ?> attribute) {
        return ((ManagedType<?>) ((SingularAttribute<?, ?>) attribute).getType()).getJavaType();
    }

    private String groupDisplayLabel(final String groupId) {
        final String[] groupMessageCodeList = { String.format(RegistryConfigurationConstants.REGISTRY_GROUP_DISPLAY_LABEL_FORMAT, groupId) };
        final DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(groupMessageCodeList, groupId);

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    private String registryDisplayLabel(final Class<?> entityType) {
        final String[] groupMessageCodeList = { String.format(RegistryConfigurationConstants.REGISTRY_NAME_DISPLAY_LABEL_FORMAT, entityType.getName()) };
        final DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(groupMessageCodeList, entityType.getSimpleName());

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
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
                String.format(RegistryConfigurationConstants.REGISTRY_FIELD_DISPLAY_LABEL_FORMAT, entityType.getName(), attributeName),
                String.format(RegistryConfigurationConstants.REGISTRY_FIELD_DISPLAY_LABEL_FORMAT, attributeName, attributeType.getName()),
                String.format(RegistryConfigurationConstants.REGISTRY_FIELD_DISPLAY_LABEL_SHORT_FORMAT, attributeType.getName())
        );
    }

    private boolean shouldSkipAttribute(final List<String> entityIgnoredPropertyList, final Attribute<?, ?> attribute) {
        return attribute.isCollection() || entityIgnoredPropertyList.contains(attribute.getName());
    }

    // TODO extract resolving of managed types or classes to separate class
    private boolean includeManagedType(final ManagedType<?> managedType, final List<String> includeDomainPatternList, final List<String> excludeDomainPatternList) {
        if (CollectionUtils.isEmpty(includeDomainPatternList)) {
            return false;
        }

        final String classFullName = managedType.getJavaType().getName();

        final boolean includeType = includeDomainPatternList.stream().anyMatch(classFullName::matches);

        if (!includeType || CollectionUtils.isEmpty(excludeDomainPatternList)) {
            return includeType;
        }

        return excludeDomainPatternList.stream().noneMatch(classFullName::matches);
    }

    private boolean isAudited(final Class<?> entityType) {
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends Annotation> enversAnnotation = (Class<? extends Annotation>) Class.forName("org.hibernate.envers.Audited");

            return entityType.isAnnotationPresent(enversAnnotation);
        }
        catch (final Exception ignored) {
            return false;
        }
    }
}
