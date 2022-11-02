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

package net.croz.nrich.registry.core.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.api.core.model.RegistryConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfigurationHolder;
import net.croz.nrich.registry.core.constants.RegistryCoreConstants;
import net.croz.nrich.registry.core.constants.RegistryEnversConstants;
import net.croz.nrich.registry.core.model.PropertyWithType;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryGroupDefinition;
import net.croz.nrich.registry.core.model.RegistryGroupDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import net.croz.nrich.registry.core.support.SingularAssociation;
import net.croz.nrich.registry.core.util.AnnotationUtil;
import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.model.SearchJoin;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class DefaultRegistryConfigurationResolverService implements RegistryConfigurationResolverService {

    private final EntityManager entityManager;

    private final RegistryConfiguration registryConfiguration;

    @Override
    public RegistryGroupDefinitionHolder resolveRegistryGroupDefinition() {
        Set<ManagedType<?>> managedTypeList = entityManager.getMetamodel().getManagedTypes();

        List<RegistryGroupDefinition> registryGroupDefinitionList = new ArrayList<>();

        registryConfiguration.getGroupDefinitionConfigurationList().forEach(registryGroupDefinition -> {
            List<ManagedTypeWrapper> includedManagedTypeList = managedTypeList.stream()
                .filter(managedType -> includeManagedType(managedType, registryGroupDefinition.getIncludeEntityPatternList(), registryGroupDefinition.getExcludeEntityPatternList()))
                .map(ManagedTypeWrapper::new)
                .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(includedManagedTypeList)) {
                return;
            }

            registryGroupDefinitionList.add(new RegistryGroupDefinition(registryGroupDefinition.getGroupId(), includedManagedTypeList));
        });

        return new RegistryGroupDefinitionHolder(registryGroupDefinitionList, registryConfiguration.getGroupDisplayOrderList());
    }

    @Override
    public Map<Class<?>, RegistryOverrideConfiguration> resolveRegistryOverrideConfigurationMap() {
        return Optional.ofNullable(registryConfiguration.getOverrideConfigurationHolderList()).orElse(Collections.emptyList())
            .stream()
            .filter(registryOverrideConfigurationHolder -> registryOverrideConfigurationHolder.getOverrideConfiguration() != null)
            .collect(Collectors.toMap(RegistryOverrideConfigurationHolder::getType, RegistryOverrideConfigurationHolder::getOverrideConfiguration));
    }

    @Override
    public RegistryDataConfigurationHolder resolveRegistryDataConfiguration() {
        RegistryGroupDefinitionHolder groupDefinitionHolder = resolveRegistryGroupDefinition();

        List<ManagedTypeWrapper> managedTypeWrapperList = groupDefinitionHolder.getGroupDefinitionList().stream()
            .map(RegistryGroupDefinition::getRegistryEntityList)
            .flatMap(List::stream)
            .collect(Collectors.toList());

        List<RegistryDataConfiguration<Object, Object>> registryDataConfigurationList = new ArrayList<>();

        managedTypeWrapperList.forEach(managedTypeWrapper -> {
            @SuppressWarnings("unchecked")
            Class<Object> type = (Class<Object>) managedTypeWrapper.getJavaType();

            registryDataConfigurationList.add(new RegistryDataConfiguration<>(type, resolveSearchConfiguration(managedTypeWrapper)));
        });

        Map<String, ManagedTypeWrapper> classNameManagedTypeWrapperMap = managedTypeWrapperList.stream().collect(Collectors.toMap(value -> value.getJavaType().getName(), Function.identity()));

        return new RegistryDataConfigurationHolder(classNameManagedTypeWrapperMap, registryDataConfigurationList);
    }

    @Override
    public RegistryHistoryConfigurationHolder resolveRegistryHistoryConfiguration() {
        ManagedType<?> revisionEntityManagedType = entityManager.getMetamodel().getManagedTypes()
            .stream()
            .filter(managedType -> AnnotationUtil.isAnnotationPresent(managedType.getJavaType(), RegistryEnversConstants.ENVERS_REVISION_ENTITY_ANNOTATION))
            .findFirst()
            .orElse(null);

        String revisionNumberPropertyName = RegistryEnversConstants.REVISION_NUMBER_PROPERTY_DEFAULT_ORIGINAL_NAME;
        Class<?> revisionNumberPropertyType = Integer.class;

        String revisionTimestampPropertyName = RegistryEnversConstants.REVISION_TIMESTAMP_PROPERTY_DEFAULT_ORIGINAL_NAME;
        // actually a long but for easier client handling it can be treated as a date
        Class<?> revisionTimestampPropertyType = Date.class;
        @SuppressWarnings("unchecked")
        Set<Attribute<?, ?>> attributes = (Set<Attribute<?, ?>>) Optional.ofNullable(revisionEntityManagedType).map(ManagedType::getAttributes).orElse(Collections.emptySet());

        List<PropertyWithType> additionalPropertyList = new ArrayList<>();
        for (Attribute<?, ?> attribute : attributes) {
            String attributeName = attribute.getName();
            Class<?> attributeType = attribute.getJavaType();

            if (!(attribute.getJavaMember() instanceof Field)) {
                continue;
            }

            Field attributeField = (Field) attribute.getJavaMember();

            if (AnnotationUtil.isAnnotationPresent(attributeField, RegistryEnversConstants.ENVERS_REVISION_NUMBER_ANNOTATION)) {
                revisionNumberPropertyName = attributeName;
                revisionNumberPropertyType = attributeType;
            }
            else if (AnnotationUtil.isAnnotationPresent(attributeField, RegistryEnversConstants.ENVERS_REVISION_TIMESTAMP_ANNOTATION)) {
                revisionTimestampPropertyName = attributeName;
                revisionTimestampPropertyType = attributeType;
            }
            else {
                additionalPropertyList.add(new PropertyWithType(attributeName, attributeName, attributeType));
            }
        }

        PropertyWithType revisionNumberProperty = new PropertyWithType(RegistryEnversConstants.REVISION_NUMBER_PROPERTY_NAME, revisionNumberPropertyName, revisionNumberPropertyType);
        PropertyWithType revisionTimestampProperty = new PropertyWithType(RegistryEnversConstants.REVISION_TIMESTAMP_PROPERTY_NAME, revisionTimestampPropertyName, revisionTimestampPropertyType);
        PropertyWithType revisionTypeProperty = new PropertyWithType(RegistryEnversConstants.REVISION_TYPE_PROPERTY_NAME, RegistryEnversConstants.REVISION_TYPE_PROPERTY_NAME, String.class);
        List<String> displayOrderList = registryConfiguration.getHistoryDisplayOrderList();

        return new RegistryHistoryConfigurationHolder(revisionNumberProperty, revisionTimestampProperty, revisionTypeProperty, additionalPropertyList, displayOrderList);
    }

    private boolean includeManagedType(ManagedType<?> managedType, List<String> includeDomainPatternList, List<String> excludeDomainPatternList) {
        if (CollectionUtils.isEmpty(includeDomainPatternList) || !(managedType instanceof IdentifiableType)
            || AnnotationUtil.isAnnotationPresent(managedType.getJavaType(), RegistryEnversConstants.ENVERS_REVISION_ENTITY_ANNOTATION)) {
            return false;
        }

        String classFullName = managedType.getJavaType().getName();

        boolean includeType = includeDomainPatternList.stream().anyMatch(classFullName::matches);

        if (!includeType || CollectionUtils.isEmpty(excludeDomainPatternList)) {
            return includeType;
        }

        return excludeDomainPatternList.stream().filter(Objects::nonNull).noneMatch(classFullName::matches);
    }

    private SearchConfiguration<Object, Object, Map<String, Object>> resolveSearchConfiguration(ManagedTypeWrapper managedTypeWrapper) {
        Class<?> type = managedTypeWrapper.getJavaType();

        return Optional.ofNullable(registryConfiguration.getOverrideConfigurationHolderList()).orElse(Collections.emptyList()).stream()
            .filter(registryOverrideConfigurationHolder -> type.equals(registryOverrideConfigurationHolder.getType()) && registryOverrideConfigurationHolder.getOverrideSearchConfiguration() != null)
            .map(RegistryOverrideConfigurationHolder::getOverrideSearchConfiguration)
            .findFirst()
            .orElse(emptySearchConfigurationWithRequiredJoinFetchList(managedTypeWrapper));
    }

    private SearchConfiguration<Object, Object, Map<String, Object>> emptySearchConfigurationWithRequiredJoinFetchList(ManagedTypeWrapper managedTypeWrapper) {
        SearchConfiguration<Object, Object, Map<String, Object>> searchConfiguration = SearchConfiguration.emptyConfigurationMatchingAny();

        List<SearchJoin<Map<String, Object>>> searchJoinList = Stream.concat(
                createSearchJoinStreamFromAssociationList(managedTypeWrapper.getSingularAssociationList(), RegistryCoreConstants.BLANK),
                createSearchJoinStreamFromAssociationList(managedTypeWrapper.getSingularEmbeddedTypeAssociationList(), managedTypeWrapper.getIdAttributeName() + RegistryCoreConstants.DOT))
            .collect(Collectors.toList());

        searchConfiguration.setJoinList(searchJoinList);

        return searchConfiguration;
    }

    private Stream<SearchJoin<Map<String, Object>>> createSearchJoinStreamFromAssociationList(List<SingularAssociation> associationList, String prefix) {
        return associationList.stream()
            .map(attribute -> attribute.isOptional() ? SearchJoin.leftJoinFetch(prefix + attribute.getPath()) : SearchJoin.innerJoinFetch(prefix + attribute.getPath()));
    }
}
