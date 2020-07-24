package net.croz.nrich.registry.data.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;
import net.croz.nrich.registry.data.util.ClassLoadingUtil;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class DefaultRegistryDataRequestConversionService implements RegistryDataRequestConversionService {

    private final ObjectMapper objectMapper;

    private final RegistryDataConfigurationHolder registryDataConfigurationHolder;

    @Override
    public Object convertEntityDataToTyped(final CreateRegistryRequest request) {
        final Class<?> type = resolveClassWithConfigurationVerification(request.getClassFullName(), RegistryDataConstants.CREATE_REQUEST_SUFFIX);

        return convertStringToInstance(request.getJsonEntityData(), type);
    }

    @Override
    public Object convertEntityDataToTyped(final UpdateRegistryRequest request) {
        final Class<?> type = resolveClassWithConfigurationVerification(request.getClassFullName(), RegistryDataConstants.UPDATE_REQUEST_SUFFIX);

        return convertStringToInstance(request.getJsonEntityData(), type);
    }

    private Class<?> resolveClassWithConfigurationVerification(final String classFullName, final String classLoadingInitialPrefix) {
        registryDataConfigurationHolder.verifyConfigurationExists(classFullName);

        final List<String> classNameList = Arrays.asList(String.format(classLoadingInitialPrefix, classFullName), String.format(RegistryDataConstants.REQUEST_SUFFIX, classFullName), classFullName);

        return ClassLoadingUtil.loadClassFromList(classNameList);
    }

    @SneakyThrows
    private Object convertStringToInstance(final String entityData, final Class<?> entityType) {
        return objectMapper.readValue(entityData, entityType);
    }
}
