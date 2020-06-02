package net.croz.nrich.registry.configuration.service.impl;

import net.croz.nrich.registry.RegistryTestConfiguration;
import net.croz.nrich.registry.configuration.model.RegistryGroupConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitWebConfig(RegistryTestConfiguration.class)
public class RegistryConfigurationServiceImplTest {

    @Autowired
    private RegistryConfigurationServiceImpl registryConfigurationService;

    @Test
    void shouldResolveRegistryConfiguration() {
        // when
        final List<RegistryGroupConfiguration> result = registryConfigurationService.readRegistryGroupConfigurationList();

        // then
        assertThat(result).isNotEmpty();
    }
}
