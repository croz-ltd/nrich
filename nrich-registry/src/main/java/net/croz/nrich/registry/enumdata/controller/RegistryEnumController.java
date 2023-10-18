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

package net.croz.nrich.registry.enumdata.controller;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.api.enumdata.model.EnumResult;
import net.croz.nrich.registry.api.enumdata.request.ListBulkRegistryEnumRequest;
import net.croz.nrich.registry.api.enumdata.request.ListRegistryEnumRequest;
import net.croz.nrich.registry.api.enumdata.service.RegistryEnumService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("${nrich.registry.enum.endpoint-path:nrich/registry/enum}")
@RestController
public class RegistryEnumController {

    private final RegistryEnumService registryEnumService;

    @PostMapping("list-bulk")
    public Map<String, List<EnumResult>> listBulk(@RequestBody @Valid ListBulkRegistryEnumRequest request) {
        return registryEnumService.listBulk(request);
    }

    @PostMapping("list")
    public List<EnumResult> list(@RequestBody @Valid ListRegistryEnumRequest request) {
        return registryEnumService.list(request);
    }
}
