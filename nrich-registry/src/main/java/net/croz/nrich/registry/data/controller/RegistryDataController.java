package net.croz.nrich.registry.data.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.croz.nrich.registry.api.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.api.data.request.CreateRegistryServiceRequest;
import net.croz.nrich.registry.api.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;
import net.croz.nrich.registry.api.data.request.UpdateRegistryServiceRequest;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import net.croz.nrich.registry.data.service.RegistryDataService;
import org.springframework.data.domain.Page;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("nrich/registry/data")
@ResponseBody
public class RegistryDataController {

    private final RegistryDataService registryDataService;

    private final RegistryDataRequestConversionService registryDataRequestConversionService;

    private final Validator validator;

    @PostMapping("list-bulk")
    public <P> Map<String, Page<P>> listBulk(@RequestBody @Valid final ListBulkRegistryRequest request) {
        return registryDataService.listBulk(request);
    }

    @PostMapping("list")
    public <P> Page<P> list(@RequestBody @Valid final ListRegistryRequest request) {
        return registryDataService.list(request);
    }

    @PostMapping("delete")
    public <T> T delete(@RequestBody @Valid final DeleteRegistryRequest request) {
        return registryDataService.delete(request);
    }

    @PostMapping("create")
    public <T> T create(@RequestBody @Valid final CreateRegistryRequest request) {
        final CreateRegistryServiceRequest serviceRequest = registryDataRequestConversionService.convertToServiceRequest(request);

        validateServiceRequest(serviceRequest);

        return registryDataService.create(serviceRequest);
    }

    @PostMapping("update")
    public <T> T update(@RequestBody @Valid final UpdateRegistryRequest request) {
        final UpdateRegistryServiceRequest serviceRequest = registryDataRequestConversionService.convertToServiceRequest(request);

        validateServiceRequest(serviceRequest);

        return registryDataService.update(serviceRequest);
    }

    @SneakyThrows
    private void validateServiceRequest(final Object serviceRequest) {
        final BindingResult errors = new BeanPropertyBindingResult(serviceRequest, "serviceRequest");

        validator.validate(serviceRequest, errors);

        if (errors.hasErrors()) {
            throw new BindException(errors);
        }
    }
}
