package net.croz.nrich.formconfiguration.api.customizer;

import java.util.Map;

@FunctionalInterface
public interface FormConfigurationMappingCustomizer {

    void customizeConfigurationMapping(Map<String, Class<?>> formConfigurationMapping);

}
