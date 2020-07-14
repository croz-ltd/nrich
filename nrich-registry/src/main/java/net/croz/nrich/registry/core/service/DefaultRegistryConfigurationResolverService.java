package net.croz.nrich.registry.core.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.api.model.RegistryConfiguration;
import net.croz.nrich.registry.api.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.model.RegistryOverrideConfigurationHolder;
import net.croz.nrich.registry.core.constants.RegistryEnversConstants;
import net.croz.nrich.registry.core.model.PropertyWithType;
import net.croz.nrich.registry.core.model.RegistryCategoryDefinition;
import net.croz.nrich.registry.core.model.RegistryCategoryDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
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
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultRegistryConfigurationResolverService implements RegistryConfigurationResolverService {

    private final EntityManager entityManager;

    private final RegistryConfiguration registryConfiguration;

    @Override
    public RegistryCategoryDefinitionHolder resolveRegistryGroupDefinition() {
        final Set<ManagedType<?>> managedTypeList = entityManager.getMetamodel().getManagedTypes();

        final List<RegistryCategoryDefinition> registryCategoryDefinitionList = new ArrayList<>();

        registryConfiguration.getRegistryCategoryDefinitionConfigurationList().forEach(registryGroupDefinition -> {
            final List<ManagedTypeWrapper> includedManagedTypeList = managedTypeList.stream()
                    .filter(managedType -> includeManagedType(managedType, registryGroupDefinition.getIncludeEntityPatternList(), registryGroupDefinition.getExcludeEntityPatternList()))
                    .map(ManagedTypeWrapper::new)
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(includedManagedTypeList)) {
                return;
            }

            registryCategoryDefinitionList.add(new RegistryCategoryDefinition(registryGroupDefinition.getRegistryCategoryId(), includedManagedTypeList));
        });

        return new RegistryCategoryDefinitionHolder(registryCategoryDefinitionList, registryConfiguration.getRegistryCategoryDisplayOrderList());
    }

    @Override
    public Map<Class<?>, RegistryOverrideConfiguration> resolveRegistryOverrideConfigurationMap() {
        return Optional.ofNullable(registryConfiguration.getRegistryOverrideConfigurationHolderList()).orElse(Collections.emptyList())
                .stream()
                .filter(registryOverrideConfigurationHolder -> registryOverrideConfigurationHolder.getRegistryOverrideConfiguration() != null)
                .collect(Collectors.toMap(RegistryOverrideConfigurationHolder::getType, RegistryOverrideConfigurationHolder::getRegistryOverrideConfiguration));
    }

    @Override
    public RegistryDataConfigurationHolder resolveRegistryDataConfiguration() {
        final RegistryCategoryDefinitionHolder groupDefinitionHolder = resolveRegistryGroupDefinition();

        final List<ManagedTypeWrapper> managedTypeWrapperList = groupDefinitionHolder.getRegistryCategoryDefinitionList().stream()
                .map(RegistryCategoryDefinition::getRegistryEntityList)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        final List<RegistryDataConfiguration<?, ?>> registryDataConfigurationList = new ArrayList<>();

        managedTypeWrapperList.forEach(managedTypeWrapper -> {
            @SuppressWarnings("unchecked")
            final Class<Object> type = (Class<Object>) managedTypeWrapper.getJavaType();

            registryDataConfigurationList.add(new RegistryDataConfiguration<>(type, resolveSearchConfiguration(managedTypeWrapper)));
        });

        final Map<String, ManagedTypeWrapper> classNameManagedTypeWrapperMap = managedTypeWrapperList.stream().collect(Collectors.toMap(value -> value.getJavaType().getName(), Function.identity()));

        return new RegistryDataConfigurationHolder(classNameManagedTypeWrapperMap, registryDataConfigurationList);
    }

    @Override
    public RegistryHistoryConfigurationHolder resolveRegistryHistoryConfiguration() {
        final ManagedType<?> revisionEntityManagedType = entityManager.getMetamodel().getManagedTypes()
                .stream()
                .filter(managedType -> AnnotationUtil.isAnnotationPresent(managedType.getJavaType(), RegistryEnversConstants.ENVERS_REVISION_ENTITY_ANNOTATION))
                .findFirst()
                .orElse(null);

        String revisionNumberPropertyName = RegistryEnversConstants.REVISION_NUMBER_PROPERTY_DEFAULT_ORIGINAL_NAME;
        Class<?> revisionNumberPropertyType = Integer.class;

        String revisionTimestampPropertyName = RegistryEnversConstants.REVISION_TIMESTAMP_PROPERTY_DEFAULT_ORIGINAL_NAME;
        // actually a long but for easier client handling it can be treated as a date
        Class<?> revisionTimestampPropertyType = Date.class;

        final List<PropertyWithType> additionalPropertyList = new ArrayList<>();
        for (final Attribute<?, ?> attribute : Optional.ofNullable(revisionEntityManagedType).map(ManagedType::getAttributes).orElse(Collections.emptySet())) {
            final String attributeName = attribute.getName();
            final Class<?> attributeType = attribute.getJavaType();

            if (!(attribute.getJavaMember() instanceof Field)) {
                continue;
            }

            final Field attributeField = (Field) attribute.getJavaMember();

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

        final PropertyWithType revisionNumberProperty = new PropertyWithType(RegistryEnversConstants.REVISION_NUMBER_PROPERTY_NAME, revisionNumberPropertyName, revisionNumberPropertyType);
        final PropertyWithType revisionTimestampProperty = new PropertyWithType(RegistryEnversConstants.REVISION_TIMESTAMP_PROPERTY_NAME, revisionTimestampPropertyName, revisionTimestampPropertyType);
        final PropertyWithType revisionTypeProperty = new PropertyWithType(RegistryEnversConstants.REVISION_TYPE_PROPERTY_NAME, RegistryEnversConstants.REVISION_TYPE_PROPERTY_NAME, String.class);

        return new RegistryHistoryConfigurationHolder(revisionNumberProperty, revisionTimestampProperty, revisionTypeProperty, additionalPropertyList, registryConfiguration.getRegistryHistoryDisplayList());
    }

    private boolean includeManagedType(final ManagedType<?> managedType, final List<String> includeDomainPatternList, final List<String> excludeDomainPatternList) {
        if (CollectionUtils.isEmpty(includeDomainPatternList) || !(managedType instanceof IdentifiableType) || AnnotationUtil.isAnnotationPresent(managedType.getJavaType(), RegistryEnversConstants.ENVERS_REVISION_ENTITY_ANNOTATION)) {
            return false;
        }

        final String classFullName = managedType.getJavaType().getName();

        final boolean includeType = includeDomainPatternList.stream().anyMatch(classFullName::matches);

        if (!includeType || CollectionUtils.isEmpty(excludeDomainPatternList)) {
            return includeType;
        }

        return excludeDomainPatternList.stream().noneMatch(classFullName::matches);
    }

    private SearchConfiguration<Object, Object, Map<String, Object>> resolveSearchConfiguration(final ManagedTypeWrapper managedTypeWrapper) {
        final Class<?> type = managedTypeWrapper.getJavaType();

        return Optional.ofNullable(registryConfiguration.getRegistryOverrideConfigurationHolderList()).orElse(Collections.emptyList()).stream()
                .filter(registryOverrideConfigurationHolder -> type.equals(registryOverrideConfigurationHolder.getType()) && registryOverrideConfigurationHolder.getRegistryDataOverrideSearchConfiguration() != null)
                .map(RegistryOverrideConfigurationHolder::getRegistryDataOverrideSearchConfiguration)
                .findFirst()
                .orElse(emptySearchConfigurationWithRequiredJoinFetchList(managedTypeWrapper));
    }

    private SearchConfiguration<Object, Object, Map<String, Object>> emptySearchConfigurationWithRequiredJoinFetchList(final ManagedTypeWrapper managedTypeWrapper) {
        final SearchConfiguration<Object, Object, Map<String, Object>> searchConfiguration = SearchConfiguration.emptyConfigurationMatchingAny();

        final List<SearchJoin<Map<String, Object>>> searchJoinList = managedTypeWrapper.getSingularAssociationList().stream()
                .map(singularAttribute -> singularAttribute.isOptional() ? SearchJoin.<Map<String, Object>>leftJoinFetch(singularAttribute.getName()) : SearchJoin.<Map<String, Object>>innerJoinFetch(singularAttribute.getName()))
                .collect(Collectors.toList());

        searchConfiguration.setJoinList(searchJoinList);

        return searchConfiguration;
    }
}
