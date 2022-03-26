package net.croz.nrich.registry.data.customizer;

import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.registry.data.stub.RegistryTestEntityUpdateRequest;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenFormConfiguration;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.croz.nrich.registry.data.testutil.RegistryDataMapGeneratingUtil.createFormConfigurationMap;
import static org.assertj.core.api.Assertions.assertThat;

class RegistryDataFormConfigurationMappingCustomizerTest {

    private static final List<Class<?>> REGISTRY_CLASS_LIST = Arrays.asList(RegistryTestEntity.class, RegistryTestEntityWithOverriddenFormConfiguration.class);

    private final RegistryDataFormConfigurationMappingCustomizer formConfigurationMappingCustomizer = new RegistryDataFormConfigurationMappingCustomizer(REGISTRY_CLASS_LIST);

    @Test
    void shouldResolveRegistryFormConfigurationForEntityWithoutCreateRequest() {
        // given
        Class<?> entity = RegistryTestEntity.class;
        String formId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, entity.getName(), RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX);
        Map<String, Class<?>> formConfiguration = new HashMap<>();

        // when
        formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfiguration);

        // and when
        Class<?> registryCreateClass = formConfiguration.get(formId);

        // then
        assertThat(registryCreateClass).isEqualTo(RegistryTestEntity.class);
    }

    @Test
    void shouldResolveRegistryFormConfigurationForEntityWithoutUpdateRequest() {
        // given
        Class<?> entity = RegistryTestEntity.class;
        String formId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, entity.getName(), RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX);
        Map<String, Class<?>> formConfiguration = new HashMap<>();

        // when
        formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfiguration);

        // and when
        Class<?> registryCreateClass = formConfiguration.get(formId);

        // then
        assertThat(registryCreateClass).isEqualTo(RegistryTestEntityUpdateRequest.class);
    }

    @Test
    void shouldNotAddFormConfigurationForExistingCreateRequest() {
        // given
        Class<?> entity = RegistryTestEntity.class;
        String formId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, entity.getName(), RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX);
        Map<String, Class<?>> formConfiguration = createFormConfigurationMap(formId, Object.class);

        // when
        formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfiguration);

        // and when
        Class<?> registryCreateClass = formConfiguration.get(formId);

        // then
        assertThat(registryCreateClass).isEqualTo(Object.class);
    }

    @Test
    void shouldNotAddFormConfigurationForExistingUpdateRequest() {
        // given
        Class<?> entity = RegistryTestEntity.class;
        String formId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, entity.getName(), RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX);
        Map<String, Class<?>> formConfiguration = createFormConfigurationMap(formId, Object.class);

        // when
        formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfiguration);

        // and when
        Class<?> registryCreateClass = formConfiguration.get(formId);

        // then
        assertThat(registryCreateClass).isEqualTo(Object.class);
    }
}
