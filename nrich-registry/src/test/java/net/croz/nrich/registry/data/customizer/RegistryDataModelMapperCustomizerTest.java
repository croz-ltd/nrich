package net.croz.nrich.registry.data.customizer;

import net.croz.nrich.registry.api.core.customizer.ModelMapperType;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static net.croz.nrich.registry.data.testutil.RegistryDataModelMapperCustomizerGeneratingUtil.createRegistryOverrideConfigurationHolderList;
import static net.croz.nrich.registry.data.testutil.RegistryDataModelMapperCustomizerGeneratingUtil.createRegistryTestEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class RegistryDataModelMapperCustomizerTest {

    private final RegistryDataModelMapperCustomizer customizer = new RegistryDataModelMapperCustomizer(createRegistryOverrideConfigurationHolderList());

    private final ModelMapper modelMapper = new ModelMapper();

    @Test
    void shouldNotFailForNullConfigurationHolderList() {
        // given
        RegistryDataModelMapperCustomizer customizerWithNullList = new RegistryDataModelMapperCustomizer(null);

        // when
        Exception thrown = catchException(() -> customizerWithNullList.customize(ModelMapperType.DATA, modelMapper));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldNotCustomizeForNonRegistryModelMapperType() {
        // given
        ModelMapper modelMapperMock = mock(ModelMapper.class);

        // when
        customizer.customize(ModelMapperType.BASE, modelMapperMock);

        // then
        verifyNoInteractions(modelMapperMock);
    }

    @Test
    void shouldSkipMappingIgnoredPropertyList() {
        // given
        Integer age = 1111;
        RegistryTestEntity source = createRegistryTestEntity();
        RegistryTestEntity destination = new RegistryTestEntity();
        destination.setAge(age);

        // when
        customizer.customize(ModelMapperType.DATA, modelMapper);

        // and when
        modelMapper.map(source, destination);

        // then
        assertThat(destination.getAge()).isEqualTo(age);
        assertThat(destination.getName()).isEqualTo(source.getName());
        assertThat(destination.getId()).isEqualTo(source.getId());
    }
}
