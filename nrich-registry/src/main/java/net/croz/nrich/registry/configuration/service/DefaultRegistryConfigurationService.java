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

package net.croz.nrich.registry.configuration.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.api.configuration.model.RegistryEntityConfiguration;
import net.croz.nrich.registry.api.configuration.model.RegistryGroupConfiguration;
import net.croz.nrich.registry.api.configuration.model.property.JavascriptType;
import net.croz.nrich.registry.api.configuration.model.property.RegistryPropertyConfiguration;
import net.croz.nrich.registry.api.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.configuration.comparator.RegistryGroupConfigurationComparator;
import net.croz.nrich.registry.configuration.comparator.RegistryPropertyComparator;
import net.croz.nrich.registry.configuration.constants.RegistryConfigurationConstants;
import net.croz.nrich.registry.configuration.util.JavaToJavascriptTypeConversionUtil;
import net.croz.nrich.registry.core.constants.RegistryCoreConstants;
import net.croz.nrich.registry.core.constants.RegistryEnversConstants;
import net.croz.nrich.registry.core.model.PropertyWithType;
import net.croz.nrich.registry.core.model.RegistryGroupDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import net.croz.nrich.registry.core.util.AnnotationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultRegistryConfigurationService implements RegistryConfigurationService {

    private final MessageSource messageSource;

    private final List<String> readOnlyPropertyList;

    private final RegistryGroupDefinitionHolder registryGroupDefinitionHolder;

    private final RegistryHistoryConfigurationHolder registryHistoryConfiguration;

    private final Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap;

    @Cacheable("nrich.registryConfiguration.cache")
    @Override
    public List<RegistryGroupConfiguration> fetchRegistryGroupConfigurationList() {
        List<RegistryGroupConfiguration> registryGroupConfigurationList = new ArrayList<>();

        List<RegistryPropertyConfiguration> registryPropertyHistoryConfigurationList = resolveHistoryPropertyList(registryHistoryConfiguration);

        registryGroupDefinitionHolder.getGroupDefinitionList().forEach(registryGroupDefinition -> {
            String registryGroupIdDisplayName = groupDisplayLabel(registryGroupDefinition.getRegistryGroupId());

            List<RegistryEntityConfiguration> configurationList = registryGroupDefinition.getRegistryEntityList().stream()
                .map(managedType -> resolveRegistryConfiguration(registryGroupDefinition.getRegistryGroupId(), managedType, registryPropertyHistoryConfigurationList))
                .sorted(Comparator.comparing(RegistryEntityConfiguration::getClassFullName))
                .collect(Collectors.toList());

            RegistryGroupConfiguration registryConfiguration = new RegistryGroupConfiguration(registryGroupDefinition.getRegistryGroupId(), registryGroupIdDisplayName, configurationList);

            registryGroupConfigurationList.add(registryConfiguration);
        });

        registryGroupConfigurationList.sort(new RegistryGroupConfigurationComparator(registryGroupDefinitionHolder.getGroupDisplayOrderList()));

        return registryGroupConfigurationList;
    }

    private RegistryEntityConfiguration resolveRegistryConfiguration(String groupId, ManagedTypeWrapper managedTypeWrapper, List<RegistryPropertyConfiguration> historyPropertyConfigurationList) {
        Class<?> entityType = managedTypeWrapper.getJavaType();
        RegistryOverrideConfiguration registryOverrideConfiguration = resolveRegistryOverrideConfiguration(entityType, registryOverrideConfigurationMap);

        String registryDisplayName = registryDisplayLabel(entityType);
        boolean isHistoryAvailable = registryOverrideConfiguration.isHistoryAvailable() || isAudited(entityType);
        List<RegistryPropertyConfiguration> registryPropertyConfigurationList = resolveRegistryPropertyListForType(managedTypeWrapper, registryOverrideConfiguration);
        List<RegistryPropertyConfiguration> registryEmbeddedIdPropertyConfigurationList = resolverEmbeddedIdPropertyConfigurationList(managedTypeWrapper, registryOverrideConfiguration);
        List<String> registryPropertyDisplayOrderList = Optional.ofNullable(registryOverrideConfiguration.getPropertyDisplayOrderList()).orElse(Collections.emptyList());

        registryPropertyConfigurationList.sort(new RegistryPropertyComparator(registryPropertyDisplayOrderList));

        return RegistryEntityConfiguration.builder()
            .groupId(groupId)
            .classFullName(entityType.getName())
            .name(entityType.getSimpleName())
            .displayName(registryDisplayName)
            .propertyConfigurationList(registryPropertyConfigurationList)
            .embeddedIdPropertyConfigurationList(registryEmbeddedIdPropertyConfigurationList)
            .historyPropertyConfigurationList(historyPropertyConfigurationList)
            .readOnly(registryOverrideConfiguration.isReadOnly())
            .creatable(registryOverrideConfiguration.isCreatable())
            .updateable(registryOverrideConfiguration.isUpdateable())
            .deletable(registryOverrideConfiguration.isDeletable())
            .isHistoryAvailable(isHistoryAvailable)
            .isIdentifierAssigned(managedTypeWrapper.isIdentifierAssigned())
            .isIdClassIdentity(managedTypeWrapper.isIdClassIdentifier())
            .isEmbeddedIdentity(managedTypeWrapper.isEmbeddedIdentifier())
            .idClassPropertyNameList(managedTypeWrapper.getIdClassPropertyNameList())
            .build();
    }

    private List<RegistryPropertyConfiguration> resolveRegistryPropertyListForType(ManagedTypeWrapper managedTypeWrapper, RegistryOverrideConfiguration registryOverrideConfiguration) {
        Predicate<String> isIdAttributePredicate = attributeName -> attributeName.equals(managedTypeWrapper.getIdAttributeName())
            || managedTypeWrapper.getIdClassPropertyNameList().contains(attributeName);

        return resolveManagedTypePropertyList(
            managedTypeWrapper.getIdentifiableType(), managedTypeWrapper.getIdentifiableType().getJavaType(), null,
            isIdAttributePredicate, !managedTypeWrapper.isIdentifierAssigned(), registryOverrideConfiguration
        );
    }

    private List<RegistryPropertyConfiguration> resolverEmbeddedIdPropertyConfigurationList(ManagedTypeWrapper managedTypeWrapper, RegistryOverrideConfiguration registryOverrideConfiguration) {
        if (!managedTypeWrapper.isEmbeddedIdentifier()) {
            return Collections.emptyList();
        }

        return resolveManagedTypePropertyList(
            managedTypeWrapper.getEmbeddableIdType(), managedTypeWrapper.getIdentifiableType().getJavaType(), managedTypeWrapper.getIdAttributeName(),
            attribute -> false, true, registryOverrideConfiguration
        );
    }

    private List<RegistryPropertyConfiguration> resolveManagedTypePropertyList(ManagedType<?> managedType, Class<?> entityType, String prefix, Predicate<String> isIdAttributePredicate,
                                                                               boolean isIdReadOnly, RegistryOverrideConfiguration registryOverrideConfiguration) {
        List<String> ignoredPropertyList = Optional.ofNullable(registryOverrideConfiguration.getIgnoredPropertyList()).orElse(Collections.emptyList());
        List<String> readOnlyOverridePropertyList = Optional.ofNullable(registryOverrideConfiguration.getNonEditablePropertyList()).orElse(Collections.emptyList());
        List<String> nonSortablePropertyList = Optional.ofNullable(registryOverrideConfiguration.getNonSortablePropertyList()).orElse(Collections.emptyList());
        List<String> nonSearchablePropertyList = Optional.ofNullable(registryOverrideConfiguration.getNonSearchablePropertyList()).orElse(Collections.emptyList());

        List<RegistryPropertyConfiguration> registryPropertyConfigurationList = new ArrayList<>();

        managedType.getAttributes().forEach(attribute -> {
            if (shouldSkipAttribute(ignoredPropertyList, attribute)) {
                return;
            }

            String attributeName = prefix == null ? attribute.getName() : String.format(RegistryConfigurationConstants.REGISTRY_PROPERTY_PREFIX_FORMAT, prefix, attribute.getName());
            Class<?> attributeType = attribute.getJavaType();

            boolean isIdAttribute = isIdAttributePredicate.test(attributeName);
            boolean isSingularAssociation = attribute.isAssociation() && attribute instanceof SingularAttribute;
            Class<?> singularAssociationReferencedClass = isSingularAssociation ? resolveSingularAssociationReferencedClass(attribute) : null;

            boolean isReadOnly = isIdAttribute ? isIdReadOnly : readOnlyPropertyList.contains(attributeName) || readOnlyOverridePropertyList.contains(attributeName);
            boolean isSortable = !nonSortablePropertyList.contains(attributeName);
            boolean isSearchable = !nonSearchablePropertyList.contains(attributeName);

            RegistryPropertyConfiguration registryPropertyConfiguration = resolveRegistryPropertyConfiguration(
                entityType.getName(), attributeType, attributeName, isIdAttribute, isSingularAssociation,
                singularAssociationReferencedClass, isReadOnly, isSortable, isSearchable
            );

            registryPropertyConfigurationList.add(registryPropertyConfiguration);

        });

        return registryPropertyConfigurationList;
    }

    private List<RegistryPropertyConfiguration> resolveHistoryPropertyList(RegistryHistoryConfigurationHolder registryHistoryConfiguration) {
        List<PropertyWithType> historyPropertyList = new ArrayList<>();

        historyPropertyList.add(registryHistoryConfiguration.getRevisionNumberProperty());
        historyPropertyList.add(registryHistoryConfiguration.getRevisionTimestampProperty());
        historyPropertyList.add(registryHistoryConfiguration.getRevisionTypeProperty());
        historyPropertyList.addAll(registryHistoryConfiguration.getRevisionAdditionalPropertyList());

        return historyPropertyList.stream()
            .map(propertyWithType -> resolveRegistryPropertyConfiguration(
                    RegistryConfigurationConstants.REGISTRY_REVISION_ENTITY_PREFIX, propertyWithType.getType(), propertyWithType.getName(), false, false,
                    null, true, true, false
                )
            )
            .sorted(new RegistryPropertyComparator(registryHistoryConfiguration.getPropertyDisplayList()))
            .collect(Collectors.toList());
    }

    private RegistryPropertyConfiguration resolveRegistryPropertyConfiguration(String entityTypePrefix, Class<?> attributeType, String attributeName, boolean isIdAttribute,
                                                                               boolean isSingularAssociation, Class<?> singularAssociationReferencedClass, boolean isReadOnly,
                                                                               boolean isSortable, boolean isSearchable) {
        JavascriptType javascriptType = JavaToJavascriptTypeConversionUtil.fromJavaType(attributeType);
        boolean isDecimal = JavaToJavascriptTypeConversionUtil.isDecimal(attributeType);

        String attributeDisplayName = convertToDisplayValue(attributeName);
        String formLabel = formLabel(entityTypePrefix, attributeType, attributeName, attributeDisplayName);
        String columnHeader = columnHeader(entityTypePrefix, attributeType, attributeName, attributeDisplayName);

        return RegistryPropertyConfiguration.builder()
            .name(attributeName)
            .originalType(attributeType.getName())
            .javascriptType(javascriptType)
            .isDecimal(isDecimal)
            .isSingularAssociation(isSingularAssociation)
            .singularAssociationReferencedClass(Optional.ofNullable(singularAssociationReferencedClass).map(Class::getName).orElse(null))
            .isId(isIdAttribute)
            .formLabel(formLabel)
            .columnHeader(columnHeader)
            .editable(!isReadOnly)
            .searchable(isSearchable)
            .sortable(isSortable)
            .build();
    }

    private Class<?> resolveSingularAssociationReferencedClass(Attribute<?, ?> attribute) {
        return ((ManagedType<?>) ((SingularAttribute<?, ?>) attribute).getType()).getJavaType();
    }

    private String groupDisplayLabel(String groupId) {
        String[] groupMessageCodeList = { String.format(RegistryConfigurationConstants.REGISTRY_GROUP_DISPLAY_LABEL_FORMAT, groupId) };
        DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(groupMessageCodeList, groupId);

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    private String registryDisplayLabel(Class<?> entityType) {
        String[] groupMessageCodeList = { String.format(RegistryConfigurationConstants.REGISTRY_NAME_DISPLAY_LABEL_FORMAT, entityType.getName()) };
        DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(groupMessageCodeList, entityType.getSimpleName());

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    private String formLabel(String entityTypePrefix, Class<?> attributeType, String attributeName, String attributeDisplayName) {
        String[] messageCodeList = labelMessageCodeList(entityTypePrefix, attributeType, attributeName).toArray(new String[0]);
        DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(messageCodeList, attributeDisplayName);

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    private String columnHeader(String entityTypePrefix, Class<?> attributeType, String attributeName, String attributeDisplayName) {
        List<String> headerMessageCodeList = new ArrayList<>();

        headerMessageCodeList.add(String.format(RegistryConfigurationConstants.REGISTRY_COLUMN_HEADER_FORMAT, entityTypePrefix, attributeName));
        headerMessageCodeList.addAll(labelMessageCodeList(entityTypePrefix, attributeType, attributeName));

        DefaultMessageSourceResolvable messageSourceResolvable = new DefaultMessageSourceResolvable(headerMessageCodeList.toArray(new String[0]), attributeDisplayName);

        return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    }

    private List<String> labelMessageCodeList(String entityTypePrefix, Class<?> attributeType, String attributeName) {
        return Arrays.asList(
            String.format(RegistryConfigurationConstants.REGISTRY_FIELD_DISPLAY_LABEL_FORMAT, entityTypePrefix, attributeName),
            String.format(RegistryConfigurationConstants.REGISTRY_FIELD_DISPLAY_LABEL_FORMAT, attributeName, attributeType.getName()),
            String.format(RegistryConfigurationConstants.REGISTRY_FIELD_DISPLAY_LABEL_SHORT_FORMAT, attributeType.getName())
        );
    }

    private boolean shouldSkipAttribute(List<String> entityIgnoredPropertyList, Attribute<?, ?> attribute) {
        return attribute.isCollection() || entityIgnoredPropertyList.contains(attribute.getName());
    }

    private boolean isAudited(Class<?> entityType) {
        return AnnotationUtil.isAnnotationPresent(entityType, RegistryEnversConstants.ENVERS_AUDITED_ANNOTATION);
    }

    private RegistryOverrideConfiguration resolveRegistryOverrideConfiguration(Class<?> type, Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap) {
        if (registryOverrideConfigurationMap == null || registryOverrideConfigurationMap.get(type) == null) {
            return RegistryOverrideConfiguration.defaultConfiguration();
        }

        return registryOverrideConfigurationMap.get(type);
    }

    private String convertToDisplayValue(String attributeName) {
        List<String> attributeWordList = Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(attributeName))
            .map(value -> value.trim().toLowerCase(Locale.ROOT))
            .filter(value -> !RegistryCoreConstants.DOT.equals(value))
            .collect(Collectors.toList());

        return StringUtils.capitalize(String.join(RegistryCoreConstants.SPACE, attributeWordList));
    }
}
