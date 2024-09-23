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

package net.croz.nrich.registry.core.service;

import net.croz.nrich.registry.api.core.model.RegistryConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryGroupDefinitionConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryGroupDefinition;
import net.croz.nrich.registry.core.model.RegistryGroupDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;
import net.croz.nrich.registry.core.stub.RegistryConfigurationRegularTestEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static net.croz.nrich.registry.core.testutil.RegistryConfigurationGeneratingUtil.createMetamodel;
import static net.croz.nrich.registry.core.testutil.RegistryConfigurationGeneratingUtil.createRegistryGroupDefinitionConfigurationList;
import static net.croz.nrich.registry.core.testutil.RegistryConfigurationGeneratingUtil.createRegistryOverrideConfigurationList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class DefaultRegistryConfigurationResolverServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private RegistryConfiguration registryConfiguration;

    @InjectMocks
    private DefaultRegistryConfigurationResolverService defaultRegistryConfigurationResolverService;

    @Test
    void shouldResolveRegistryGroupDefinition() {
        // given
        doReturn(createMetamodel()).when(entityManager).getMetamodel();
        doReturn(createRegistryGroupDefinitionConfigurationList("^net.croz.nrich.registry.core.stub.*$", null)).when(registryConfiguration).getGroupDefinitionConfigurationList();

        // when
        RegistryGroupDefinitionHolder result = defaultRegistryConfigurationResolverService.resolveRegistryGroupDefinition();

        // then
        assertThat(result).isNotNull();
        assertThat(result.groupDefinitionList()).hasSize(1);

        RegistryGroupDefinition groupDefinition = result.groupDefinitionList().get(0);

        assertThat(groupDefinition.registryGroupId()).isEqualTo("groupId");
        assertThat(groupDefinition.registryEntityList()).hasSize(1);
        assertThat(groupDefinition.registryEntityList()).extracting("identifiableType.javaType").containsExactly(RegistryConfigurationRegularTestEntity.class);
    }

    @Test
    void shouldExcludeRegistryGroupConfiguration() {
        // given
        List<RegistryGroupDefinitionConfiguration> configurationList = createRegistryGroupDefinitionConfigurationList("^net.croz.nrich.registry.core.stub.*$", "^net.croz.nrich.registry.core.stub.*$");
        doReturn(createMetamodel()).when(entityManager).getMetamodel();
        doReturn(configurationList).when(registryConfiguration).getGroupDefinitionConfigurationList();

        // when
        RegistryGroupDefinitionHolder result = defaultRegistryConfigurationResolverService.resolveRegistryGroupDefinition();

        // then
        assertThat(result).isNotNull();
        assertThat(result.groupDefinitionList()).isEmpty();
    }

    @Test
    void shouldResolveRegistryOverrideConfigurationMap() {
        // given
        List<RegistryOverrideConfigurationHolder> registryOverrideConfigurationHolderList = createRegistryOverrideConfigurationList();
        doReturn(registryOverrideConfigurationHolderList).when(registryConfiguration).getOverrideConfigurationHolderList();

        // when
        Map<Class<?>, RegistryOverrideConfiguration> result = defaultRegistryConfigurationResolverService.resolveRegistryOverrideConfigurationMap();

        // then
        assertThat(result).containsKey(RegistryConfigurationRegularTestEntity.class);
        assertThat(result.get(RegistryConfigurationRegularTestEntity.class)).usingRecursiveComparison().isEqualTo(registryOverrideConfigurationHolderList.get(0).getOverrideConfiguration());
    }

    @Test
    void shouldResolveRegistryDataConfiguration() {
        // given
        doReturn(createMetamodel()).when(entityManager).getMetamodel();
        doReturn(createRegistryGroupDefinitionConfigurationList("^net.croz.nrich.registry.core.stub.*$", null)).when(registryConfiguration).getGroupDefinitionConfigurationList();

        // when
        RegistryDataConfigurationHolder result = defaultRegistryConfigurationResolverService.resolveRegistryDataConfiguration();

        // then
        assertThat(result).isNotNull();
        assertThat(result.findRegistryConfigurationForClass(RegistryConfigurationRegularTestEntity.class.getName())).isNotNull();
    }

    @Test
    void shouldResolveRegistryHistoryConfiguration() {
        // given
        doReturn(createMetamodel()).when(entityManager).getMetamodel();

        // when
        RegistryHistoryConfigurationHolder result = defaultRegistryConfigurationResolverService.resolveRegistryHistoryConfiguration();

        // then
        assertThat(result).isNotNull();

        assertThat(result.revisionNumberProperty().originalName()).isEqualTo("customRevisionNumber");
        assertThat(result.revisionNumberProperty().type()).isEqualTo(Integer.class);
        assertThat(result.revisionTimestampProperty().originalName()).isEqualTo("customRevisionTimestamp");
        assertThat(result.revisionTimestampProperty().type()).isEqualTo(Instant.class);
        assertThat(result.revisionAdditionalPropertyList()).hasSize(1);
        assertThat(result.revisionAdditionalPropertyList()).extracting("name").containsExactly("customProperty");
        assertThat(result.revisionAdditionalPropertyList()).extracting("type").containsExactly(String.class);
    }
}
