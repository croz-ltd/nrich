package net.croz.nrich.registry.starter.configuration.stub;

import net.croz.nrich.registry.api.model.RegistryConfiguration;
import net.croz.nrich.registry.api.model.RegistryGroupDefinitionConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration(proxyBeanMethods = false)
public class RegistryUserConfiguration {

    @Bean
    public RegistryConfiguration registryConfiguration() {
        final RegistryConfiguration registryConfiguration = new RegistryConfiguration();

        final RegistryGroupDefinitionConfiguration registryDataConfigurationGroup = new RegistryGroupDefinitionConfiguration();

        registryDataConfigurationGroup.setRegistryGroupId("DATA");
        registryDataConfigurationGroup.setIncludeEntityPatternList(Collections.singletonList("^net.croz.nrich.registry.data.stub.*$"));

        registryConfiguration.setRegistryGroupDefinitionConfigurationList(Collections.singletonList(registryDataConfigurationGroup));

        return registryConfiguration;
    }

}
