package net.croz.nrich.registry.configuration.controller;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.registry.configuration.model.RegistryGroupConfiguration;
import net.croz.nrich.registry.configuration.service.RegistryConfigurationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

// TODO what about form configuration, ideally maybe enable dynamic registration of formId class mapping instead of explicit dependency on form-configuration module?
@RequiredArgsConstructor
@RequestMapping("nrich/registry/configuration")
@ResponseBody
public class RegistryConfigurationController {

    private final RegistryConfigurationService registryConfigurationService;

    @PostMapping("fetch")
    public List<RegistryGroupConfiguration> fetch() {
        return registryConfigurationService.fetchRegistryGroupConfiguration();
    }
}
