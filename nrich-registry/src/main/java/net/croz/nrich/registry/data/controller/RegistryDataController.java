package net.croz.nrich.registry.data.controller;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.data.request.ListRegistryRequest;
import net.croz.nrich.registry.data.service.RegistryDataService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("nrichRegistryData")
@ResponseBody
public class RegistryDataController {

    private final RegistryDataService registryDataService;

    @PostMapping("registryList")
    public Page<?> registryList(@RequestBody @Valid ListRegistryRequest request) {
        return registryDataService.registryList(request);
    }
}
