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

package net.croz.nrich.registry.configuration.controller;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.api.configuration.model.RegistryGroupConfiguration;
import net.croz.nrich.registry.api.configuration.service.RegistryConfigurationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("${nrich.registry.configuration.endpoint-path:nrich/registry/configuration}")
@RestController
public class RegistryConfigurationController {

    private final RegistryConfigurationService registryConfigurationService;

    @PostMapping("fetch")
    public List<RegistryGroupConfiguration> fetch() {
        return registryConfigurationService.fetchRegistryGroupConfigurationList();
    }
}
