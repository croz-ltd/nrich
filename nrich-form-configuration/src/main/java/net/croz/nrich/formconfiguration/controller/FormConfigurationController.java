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

package net.croz.nrich.formconfiguration.controller;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.formconfiguration.api.model.FormConfiguration;
import net.croz.nrich.formconfiguration.api.request.FetchFormConfigurationRequest;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("${nrich.form-configuration.endpoint-path:nrich/form/configuration}")
public class FormConfigurationController {

    private final FormConfigurationService formConfigurationService;

    @ResponseBody
    @PostMapping("fetch-all")
    public List<FormConfiguration> fetchAll() {
        return formConfigurationService.fetchFormConfigurationList();
    }

    @ResponseBody
    @PostMapping("fetch")
    public List<FormConfiguration> fetch(@RequestBody @Valid FetchFormConfigurationRequest request) {
        return formConfigurationService.fetchFormConfigurationList(request.getFormIdList());
    }
}
