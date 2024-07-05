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

package net.croz.nrich.registry.core.testutil;

import lombok.SneakyThrows;
import net.croz.nrich.registry.api.core.model.RegistryGroupDefinitionConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfigurationHolder;
import net.croz.nrich.registry.core.stub.RegistryConfigurationEnversTestEntity;
import net.croz.nrich.registry.core.stub.RegistryConfigurationRegularTestEntity;

import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.IdentifiableType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public final class RegistryConfigurationGeneratingUtil {

    private RegistryConfigurationGeneratingUtil() {
    }

    public static Metamodel createMetamodel() {
        Metamodel metamodel = mock(Metamodel.class);

        ManagedType<?> enversEntity = mock(IdentifiableType.class);
        ManagedType<?> nonIdentifiableEntity = mock(ManagedType.class);
        ManagedType<?> entity = mock(IdentifiableType.class);

        doReturn(RegistryConfigurationEnversTestEntity.class).when(enversEntity).getJavaType();
        doReturn(createAttributeList()).when(enversEntity).getAttributes();
        doReturn(RegistryConfigurationRegularTestEntity.class).when(entity).getJavaType();

        doReturn(new HashSet<>(Arrays.asList(enversEntity, nonIdentifiableEntity, entity))).when(metamodel).getManagedTypes();

        return metamodel;
    }

    public static List<RegistryGroupDefinitionConfiguration> createRegistryGroupDefinitionConfigurationList(String includePatterns, String excludePatterns) {
        RegistryGroupDefinitionConfiguration groupDefinitionConfiguration = new RegistryGroupDefinitionConfiguration();

        groupDefinitionConfiguration.setGroupId("groupId");
        groupDefinitionConfiguration.setIncludeEntityPatternList(Collections.singletonList(includePatterns));
        groupDefinitionConfiguration.setExcludeEntityPatternList(Collections.singletonList(excludePatterns));

        return Collections.singletonList(groupDefinitionConfiguration);
    }

    public static List<RegistryOverrideConfigurationHolder> createRegistryOverrideConfigurationList() {
        RegistryOverrideConfiguration overrideConfiguration = new RegistryOverrideConfiguration();

        RegistryOverrideConfigurationHolder registryOverrideConfigurationHolder = RegistryOverrideConfigurationHolder.builder()
            .type(RegistryConfigurationRegularTestEntity.class)
            .overrideConfiguration(overrideConfiguration)
            .build();

        return Collections.singletonList(registryOverrideConfigurationHolder);
    }

    private static Set<Attribute<?, ?>> createAttributeList() {
        List<Attribute<?, ?>> attributeList = new ArrayList<>(createAttributesFromClass());

        attributeList.add(createAttributeWithMember("customProperty", String.class, mock(Member.class)));

        return new HashSet<>(attributeList);
    }

    // there are some issue with using mockito-inline for mocking of final classes so in this case it is easier to add and resolve properties from class
    @SneakyThrows
    private static List<Attribute<?, ?>> createAttributesFromClass() {
        List<Attribute<?, ?>> attributeList = new ArrayList<>();

        Class<?> type = RegistryConfigurationEnversTestEntity.class;

        Arrays.stream(type.getDeclaredFields())
            .filter(field -> !field.isSynthetic())
            .forEach(field -> attributeList.add(createAttributeWithMember(field.getName(), field.getType(), field)));

        return attributeList;
    }

    private static Attribute<?, ?> createAttributeWithMember(String name, Class<?> type, Member member) {
        Attribute<?, ?> attribute = mock(Attribute.class);

        doReturn(name).when(attribute).getName();
        doReturn(type).when(attribute).getJavaType();
        doReturn(member).when(attribute).getJavaMember();

        return attribute;
    }
}
