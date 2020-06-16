package net.croz.nrich.registry.data.service;

import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.service.RegistryDataFormConfigurationResolverService;
import net.croz.nrich.registry.data.util.ClassLoadingUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DefaultRegistryDataFormConfigurationResolverService implements RegistryDataFormConfigurationResolverService {

    private final List<Class<?>> registryClassList;

    private final Map<String, Class<?>> formConfigurationMap;

    public DefaultRegistryDataFormConfigurationResolverService(final List<Class<?>> registryClassList, final Map<String, Class<?>> formConfigurationMap) {
        this.registryClassList = registryClassList;
        this.formConfigurationMap = formConfigurationMap;

        registerRegistryFormConfiguration(formConfigurationMap);
    }

    @Override
    public Map<String, Class<?>> resolveRegistryFormConfiguration() {
        return formConfigurationMap;
    }

    private void registerRegistryFormConfiguration(final Map<String, Class<?>> formConfigurationMap) {
        registryClassList.forEach(registryClass -> {
            final String registryClassName = registryClass.getName();
            final String registryCreateFormId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, registryClassName, RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX);
            final String registryUpdateFormId = String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, registryClassName, RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX);

            if (formConfigurationMap.get(registryCreateFormId) == null) {
                final Class<?> createClass = resolveClass(registryClassName, RegistryDataConstants.CREATE_REQUEST_SUFFIX);

                formConfigurationMap.put(registryCreateFormId, createClass);
            }
            if (formConfigurationMap.get(registryUpdateFormId) == null) {
                final Class<?> updateClass = resolveClass(registryClassName, RegistryDataConstants.UPDATE_REQUEST_SUFFIX);

                formConfigurationMap.put(registryUpdateFormId, updateClass);
            }
        });
    }

    private Class<?> resolveClass(final String classFullName, final String classLoadingInitialPrefix) {
        final List<String> classNameList = Arrays.asList(String.format(classLoadingInitialPrefix, classFullName), String.format(RegistryDataConstants.REQUEST_SUFFIX, classFullName), classFullName);

        return ClassLoadingUtil.loadClassFromList(classNameList);
    }
}
