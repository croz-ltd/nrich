package net.croz.nrich.registry.data.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.model.RegistryDataConfiguration;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryServiceRequest;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import net.croz.nrich.registry.data.util.ClassLoadingUtil;
import net.croz.nrich.registry.data.util.RegistrySearchConfigurationUtil;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class RegistryDataRequestConversionServiceImpl implements RegistryDataRequestConversionService {

    private final ObjectMapper objectMapper;

    private final List<RegistryDataConfiguration<?, ?>> registryDataConfigurationList;

    @Override
    public CreateRegistryServiceRequest convertToServiceRequest(final CreateRegistryRequest request) {
        final Class<?> type = resolveClassWithConfigurationVerification(request.getClassFullName(), RegistryDataConstants.CREATE_REQUEST_SUFFIX);

        final CreateRegistryServiceRequest serviceRequest = new CreateRegistryServiceRequest();

        serviceRequest.setClassFullName(request.getClassFullName());
        serviceRequest.setEntityData(convertStringToInstance(request.getEntityData(), type));

        return serviceRequest;
    }

    @Override
    public UpdateRegistryServiceRequest convertToServiceRequest(final UpdateRegistryRequest request) {
        final Class<?> type = resolveClassWithConfigurationVerification(request.getClassFullName(), RegistryDataConstants.UPDATE_REQUEST_SUFFIX);

        final UpdateRegistryServiceRequest serviceRequest = new UpdateRegistryServiceRequest();

        serviceRequest.setId(request.getId());
        serviceRequest.setClassFullName(request.getClassFullName());
        serviceRequest.setEntityData(convertStringToInstance(request.getEntityData(), type));

        return serviceRequest;
    }

    private Class<?> resolveClassWithConfigurationVerification(final String classFullName, final String classLoadingInitialPrefix) {
        RegistrySearchConfigurationUtil.verifyConfigurationExists(registryDataConfigurationList, classFullName);

        final List<String> classNameList = Arrays.asList(String.format(classLoadingInitialPrefix, classFullName), String.format(RegistryDataConstants.REQUEST_SUFFIX, classFullName), classFullName);

        return ClassLoadingUtil.loadClassFromList(classNameList);
    }

    @SneakyThrows
    private Object convertStringToInstance(final String entityData, final Class<?> entityType) {
        return objectMapper.readValue(entityData, entityType);
    }
}
