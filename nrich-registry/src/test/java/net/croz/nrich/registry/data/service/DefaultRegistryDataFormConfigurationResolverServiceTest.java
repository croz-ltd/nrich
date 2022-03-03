package net.croz.nrich.registry.data.service;

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
class DefaultRegistryDataFormConfigurationResolverServiceTest {

    @Autowired
    private DefaultRegistryDataFormConfigurationResolverService registryDataFormConfigurationResolverService;

    @Test
    void shouldResolveRegistryFormConfigurationForEntityWithoutCreateRequest() {
        // when
        Map<String, Class<?>> formConfigurationMap = registryDataFormConfigurationResolverService.resolveRegistryFormConfiguration();
        String entityName = RegistryTestEntity.class.getName();
        String formId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, entityName, RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX);

        // then
        assertThat(formConfigurationMap).isNotEmpty();

        // and when
        Class<?> registryCreateClass = formConfigurationMap.get(formId);

        // then
        assertThat(registryCreateClass).isEqualTo(RegistryTestEntity.class);
    }

    @Test
    void shouldResolveRegistryFormConfigurationForEntityWithUpdateRequest() {
        // when
        Map<String, Class<?>> formConfigurationMap = registryDataFormConfigurationResolverService.resolveRegistryFormConfiguration();
        String entityName = RegistryTestEntity.class.getName();
        String formId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, entityName, RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX);

        // then
        assertThat(formConfigurationMap).isNotEmpty();

        // and when
        Class<?> registryCreateClass = formConfigurationMap.get(formId);

        // then
        assertThat(registryCreateClass).isEqualTo(RegistryTestEntityUpdateRequest.class);
    }

    @Test
    void shouldNotAddFormConfigurationForExistingCreateRequest() {
        // when
        Map<String, Class<?>> formConfigurationMap = registryDataFormConfigurationResolverService.resolveRegistryFormConfiguration();
        String entityName = RegistryTestEntityWithOverriddenFormConfiguration.class.getName();
        String formId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, entityName, RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX);

        // then
        assertThat(formConfigurationMap).isNotEmpty();

        // and when
        Class<?> registryCreateClass = formConfigurationMap.get(formId);

        // then
        assertThat(registryCreateClass).isEqualTo(Object.class);
    }

    @Test
    void shouldNotAddFormConfigurationForExistingUpdateRequest() {
        // when
        Map<String, Class<?>> formConfigurationMap = registryDataFormConfigurationResolverService.resolveRegistryFormConfiguration();
        String entityName = RegistryTestEntityWithOverriddenFormConfiguration.class.getName();
        String formId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, entityName, RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX);

        // then
        assertThat(formConfigurationMap).isNotEmpty();

        // and when
        Class<?> registryCreateClass = formConfigurationMap.get(formId);

        // then
        assertThat(registryCreateClass).isEqualTo(Object.class);
    }
}
