package net.croz.nrich.registry.data.service.impl;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.data.stub.RegistryTestEntityUpdateRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenFormConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitWebConfig(RegistryTestConfiguration.class)
public class RegistryDataFormConfigurationResolverServiceImplTest {

    @Autowired
    private RegistryDataFormConfigurationResolverServiceImpl registryDataFormConfigurationResolverService;

    @Test
    void shouldResolveRegistryFormConfigurationForEntityWithoutCreateRequest() {
        // when
        final Map<String, Class<?>> formConfigurationMap = registryDataFormConfigurationResolverService.resolveRegistryFormConfiguration();

        // then
        assertThat(formConfigurationMap).isNotEmpty();

        // and when
        final Class<?> registryCreateClass = formConfigurationMap.get(String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, RegistryTestEntity.class.getName(), RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX));

        // then
        assertThat(registryCreateClass).isEqualTo(RegistryTestEntity.class);
    }

    @Test
    void shouldResolveRegistryFormConfigurationForEntityWithUpdateRequest() {
        // when
        final Map<String, Class<?>> formConfigurationMap = registryDataFormConfigurationResolverService.resolveRegistryFormConfiguration();

        // then
        assertThat(formConfigurationMap).isNotEmpty();

        // and when
        final Class<?> registryCreateClass = formConfigurationMap.get(String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, RegistryTestEntity.class.getName(), RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX));

        // then
        assertThat(registryCreateClass).isEqualTo(RegistryTestEntityUpdateRequest.class);
    }

    @Test
    void shouldNotAddFormConfigurationForExistingCreateRequest() {
        // when
        final Map<String, Class<?>> formConfigurationMap = registryDataFormConfigurationResolverService.resolveRegistryFormConfiguration();

        // then
        assertThat(formConfigurationMap).isNotEmpty();

        // and when
        final Class<?> registryCreateClass = formConfigurationMap.get(String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, RegistryTestEntityWithOverriddenFormConfiguration.class.getName(), RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX));

        // then
        assertThat(registryCreateClass).isEqualTo(Object.class);
    }

    @Test
    void shouldNotAddFormConfigurationForExistingUpdateRequest() {
        // when
        final Map<String, Class<?>> formConfigurationMap = registryDataFormConfigurationResolverService.resolveRegistryFormConfiguration();

        // then
        assertThat(formConfigurationMap).isNotEmpty();

        // and when
        final Class<?> registryCreateClass = formConfigurationMap.get(String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, RegistryTestEntityWithOverriddenFormConfiguration.class.getName(), RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX));

        // then
        assertThat(registryCreateClass).isEqualTo(Object.class);
    }
}
