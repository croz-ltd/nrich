package net.croz.nrich.registry.core.service.impl;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.core.model.RegistryConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryGroupDefinition;
import net.croz.nrich.registry.core.model.RegistryGroupDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.core.service.RegistryConfigurationResolverService;
import net.croz.nrich.search.model.SearchConfiguration;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.ManagedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RegistryConfigurationResolverServiceImpl implements RegistryConfigurationResolverService {

    private final EntityManager entityManager;

    private final RegistryConfiguration registryConfiguration;

    @Override
    public RegistryGroupDefinitionHolder resolveRegistryGroupDefinition() {
        final Set<ManagedType<?>> managedTypeList = entityManager.getMetamodel().getManagedTypes();

        final List<RegistryGroupDefinition> registryGroupDefinitionList = new ArrayList<>();

        registryConfiguration.getRegistryGroupDefinitionConfigurationList().forEach(registryGroupDefinition -> {
            final List<ManagedType<?>> includedManagedTypeList = managedTypeList.stream()
                    .filter(managedType -> includeManagedType(managedType, registryGroupDefinition.getIncludeEntityPatternList(), registryGroupDefinition.getExcludeEntityPatternList()))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(includedManagedTypeList)) {
                return;
            }

            registryGroupDefinitionList.add(new RegistryGroupDefinition(registryGroupDefinition.getRegistryGroupId(), includedManagedTypeList));
        });

        return new RegistryGroupDefinitionHolder(registryGroupDefinitionList, registryConfiguration.getRegistryGroupDisplayOrderList());
    }

    @Override
    public Map<Class<?>, RegistryOverrideConfiguration> resolveRegistryOverrideConfigurationMap() {
        return registryConfiguration.getEntityRegistryOverrideConfiguration();
    }

    @Override
    public RegistryDataConfigurationHolder resolveRegistryDataConfiguration() {
        final RegistryGroupDefinitionHolder groupDefinitionHolder = resolveRegistryGroupDefinition();

        final List<ManagedType<?>> managedTypeList = groupDefinitionHolder.getRegistryGroupDefinitionList().stream()
                .map(RegistryGroupDefinition::getRegistryEntityList)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        final List<RegistryDataConfiguration<?, ?>> registryDataConfigurationList = new ArrayList<>();

        managedTypeList.forEach(managedType -> {
            @SuppressWarnings("unchecked")
            final Class<Object> type = (Class<Object>) managedType.getJavaType();

            registryDataConfigurationList.add(new RegistryDataConfiguration<>(type, resolveSearchConfiguration(managedType)));
        });

        return new RegistryDataConfigurationHolder(registryDataConfigurationList);
    }

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

    private SearchConfiguration<Object, Object, Map<String, Object>> resolveSearchConfiguration(final ManagedType<?> managedType) {
        final Map<Class<?>, SearchConfiguration<?, ?, Map<String, Object>>> entitySearchOverrideMap = registryConfiguration.getEntitySearchOverrideConfigurationMap();

        final Class<?> type = managedType.getJavaType();

        SearchConfiguration<Object, Object, Map<String, Object>> searchConfiguration = SearchConfiguration.emptyConfigurationMatchingAny();
        if (entitySearchOverrideMap != null && entitySearchOverrideMap.get(type) != null) {

            @SuppressWarnings("unchecked")
            final SearchConfiguration<Object, Object, Map<String, Object>> resolvedSearchConfiguration = (SearchConfiguration<Object, Object, Map<String, Object>>) entitySearchOverrideMap.get(type);

            searchConfiguration = resolvedSearchConfiguration;
        }

        return searchConfiguration;
    }
}
