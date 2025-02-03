/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.registry.data.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.croz.nrich.registry.api.data.request.ListBulkRegistryRequest;
import net.croz.nrich.registry.api.data.request.ListRegistryRequest;
import net.croz.nrich.registry.api.data.service.RegistryDataService;
import net.croz.nrich.registry.data.request.CreateRegistryRequest;
import net.croz.nrich.registry.data.request.DeleteRegistryRequest;
import net.croz.nrich.registry.data.request.UpdateRegistryRequest;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("${nrich.registry.data.endpoint-path:nrich/registry/data}")
@RestController
public class RegistryDataController {

    private final RegistryDataService registryDataService;

    private final RegistryDataRequestConversionService registryDataRequestConversionService;

    private final Validator validator;

    @PostMapping("list-bulk")
    public Map<String, Page<Object>> listBulk(@RequestBody @Valid ListBulkRegistryRequest request) {
        return registryDataService.listBulk(request);
    }

    @PostMapping("list")
    public Page<Object> list(@RequestBody @Valid ListRegistryRequest request) {
        return registryDataService.list(request);
    }

    @PostMapping("delete")
    public void delete(@RequestBody @Valid DeleteRegistryRequest request) {
        registryDataService.delete(request.classFullName(), request.id());
    }

    @PostMapping("create")
    public Object create(@RequestBody @Valid CreateRegistryRequest request) {
        Object entityData = registryDataRequestConversionService.convertEntityDataToTyped(request);

        validateEntityData("create", CreateRegistryRequest.class, entityData);

        return registryDataService.create(request.classFullName(), entityData);
    }

    @PostMapping("update")
    public Object update(@RequestBody @Valid UpdateRegistryRequest request) {
        Object entityData = registryDataRequestConversionService.convertEntityDataToTyped(request);

        validateEntityData("update", UpdateRegistryRequest.class, entityData);

        return registryDataService.update(request.classFullName(), request.id(), entityData);
    }

    @SneakyThrows
    private void validateEntityData(String methodName, Class<?> methodParameter, Object entityData) {
        BindingResult errors = new BeanPropertyBindingResult(entityData, "entityData");

        validator.validate(entityData, errors);

        if (errors.hasErrors()) {
            throw new MethodArgumentNotValidException(new MethodParameter(this.getClass().getMethod(methodName, methodParameter), 0), errors);
        }
    }
}
