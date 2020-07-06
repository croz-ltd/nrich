package net.croz.nrich.registry.starter.configuration.stub;

import net.croz.nrich.registry.api.model.RegistryConfiguration;
import net.croz.nrich.registry.api.model.RegistryCategoryDefinitionConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration(proxyBeanMethods = false)
public class RegistryUserConfiguration {

    @Bean
    public RegistryConfiguration registryConfiguration() {
        final RegistryConfiguration registryConfiguration = new RegistryConfiguration();

        final RegistryCategoryDefinitionConfiguration registryDataConfigurationGroup = new RegistryCategoryDefinitionConfiguration();

        registryDataConfigurationGroup.setRegistryCategoryId("DATA");
        registryDataConfigurationGroup.setIncludeEntityPatternList(Collections.singletonList("^net.croz.nrich.registry.data.stub.*$"));

        registryConfiguration.setRegistryCategoryDefinitionConfigurationList(Collections.singletonList(registryDataConfigurationGroup));

        return registryConfiguration;
    }

}
