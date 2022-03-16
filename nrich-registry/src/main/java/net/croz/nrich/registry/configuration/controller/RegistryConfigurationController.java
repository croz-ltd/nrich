package net.croz.nrich.registry.configuration.controller;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.api.configuration.model.RegistryGroupConfiguration;
import net.croz.nrich.registry.api.configuration.service.RegistryConfigurationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("${nrich.registry.configuration.endpoint-path:nrich/registry/configuration}")
@ResponseBody
public class RegistryConfigurationController {

    private final RegistryConfigurationService registryConfigurationService;

    @PostMapping("fetch")
    public List<RegistryGroupConfiguration> fetch() {
        return registryConfigurationService.fetchRegistryGroupConfigurationList();
    }
}
