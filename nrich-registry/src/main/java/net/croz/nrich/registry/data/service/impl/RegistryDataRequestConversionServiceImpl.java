package net.croz.nrich.registry.data.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.model.RegistryDataConfiguration;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.CreateRegistryServiceRequest;
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
        final String classFullName = request.getClassFullName();

        RegistrySearchConfigurationUtil.verifyConfigurationExists(registryDataConfigurationList, classFullName);

        final List<String> classNameList = Arrays.asList(String.format(RegistryDataConstants.CREATE_REQUEST_SUFFIX, classFullName), String.format(RegistryDataConstants.REQUEST_SUFFIX, classFullName), classFullName);

        final Class<?> type = ClassLoadingUtil.loadClassFromList(classNameList);

        final CreateRegistryServiceRequest serviceRequest = new CreateRegistryServiceRequest();

        serviceRequest.setEntityData(convertStringToInstance(request.getEntityData(), type));
        serviceRequest.setClassFullName(classFullName);

        return serviceRequest;
    }

    @SneakyThrows
    private Object convertStringToInstance(final String entityData, final Class<?> entityType) {
        return objectMapper.readValue(entityData, entityType);
    }
}
