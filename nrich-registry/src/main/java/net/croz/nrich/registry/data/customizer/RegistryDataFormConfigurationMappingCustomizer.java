package net.croz.nrich.registry.data.customizer;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.api.customizer.FormConfigurationMappingCustomizer;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.util.ClassLoadingUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class RegistryDataFormConfigurationMappingCustomizer implements FormConfigurationMappingCustomizer {

    private final List<Class<?>> registryClassList;

    @Override
    public void customizeConfigurationMapping(Map<String, Class<?>> formConfigurationMapping) {
        registerRegistryFormConfiguration(formConfigurationMapping);
    }

    private void registerRegistryFormConfiguration(Map<String, Class<?>> formConfigurationMap) {
        registryClassList.forEach(registryClass -> {
            String registryClassName = registryClass.getName();
            String registryCreateFormId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, registryClassName, RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX);
            String registryUpdateFormId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, registryClassName, RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX);

            if (formConfigurationMap.get(registryCreateFormId) == null) {
                Class<?> createClass = resolveClass(registryClassName, RegistryDataConstants.CREATE_REQUEST_SUFFIX);

                formConfigurationMap.put(registryCreateFormId, createClass);
            }
            if (formConfigurationMap.get(registryUpdateFormId) == null) {
                Class<?> updateClass = resolveClass(registryClassName, RegistryDataConstants.UPDATE_REQUEST_SUFFIX);

                formConfigurationMap.put(registryUpdateFormId, updateClass);
            }
        });
    }

    private Class<?> resolveClass(String classFullName, String classLoadingInitialPrefix) {
        List<String> classNameList = Arrays.asList(String.format(classLoadingInitialPrefix, classFullName), String.format(RegistryDataConstants.REQUEST_SUFFIX, classFullName), classFullName);

        return ClassLoadingUtil.loadClassFromList(classNameList);
    }
}
