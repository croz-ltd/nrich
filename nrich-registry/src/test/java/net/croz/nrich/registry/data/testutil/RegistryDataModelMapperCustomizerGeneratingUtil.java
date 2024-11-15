package net.croz.nrich.registry.data.testutil;

import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfigurationHolder;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithoutAssociation;
import net.croz.nrich.registry.data.stub.RegistryTestGroupType;

import java.util.List;

public final class RegistryDataModelMapperCustomizerGeneratingUtil {

    private RegistryDataModelMapperCustomizerGeneratingUtil() {
    }

    public static List<RegistryOverrideConfigurationHolder> createRegistryOverrideConfigurationHolderList() {
        RegistryOverrideConfiguration overrideConfiguration = new RegistryOverrideConfiguration();

        overrideConfiguration.setIgnoredPropertyList(List.of("age"));

        return List.of(
            RegistryOverrideConfigurationHolder.builder()
                .type(RegistryTestGroupType.class)
                .build(),
            RegistryOverrideConfigurationHolder.builder()
                .type(RegistryTestEntityWithoutAssociation.class)
                .overrideConfiguration(null)
                .build(),
            RegistryOverrideConfigurationHolder.builder()
                .type(RegistryTestEntity.class)
                .overrideConfiguration(overrideConfiguration)
                .build()
        );
    }

    public static RegistryTestEntity createRegistryTestEntity() {
        RegistryTestEntity entity = new RegistryTestEntity();

        entity.setName("name");
        entity.setAge(10);
        entity.setId(1L);

        return entity;
    }
}
