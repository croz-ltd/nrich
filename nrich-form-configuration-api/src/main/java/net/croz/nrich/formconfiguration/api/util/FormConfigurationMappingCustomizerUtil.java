package net.croz.nrich.formconfiguration.api.util;

import net.croz.nrich.formconfiguration.api.customizer.FormConfigurationMappingCustomizer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FormConfigurationMappingCustomizerUtil {

    private FormConfigurationMappingCustomizerUtil() {
    }

    public static Map<String, Class<?>> applyCustomizerList(Map<String, Class<?>> formConfiguration, List<FormConfigurationMappingCustomizer> formConfigurationCustomizerList) {
        List<FormConfigurationMappingCustomizer> resolvedCustomizerList = Optional.ofNullable(formConfigurationCustomizerList).orElse(Collections.emptyList());
        Map<String, Class<?>> formConfigurationMapping = new LinkedHashMap<>(Optional.ofNullable(formConfiguration).orElse(new LinkedHashMap<>()));

        resolvedCustomizerList.forEach(formConfigurationMappingCustomizer -> formConfigurationMappingCustomizer.customizeConfigurationMapping(formConfigurationMapping));

        return formConfigurationMapping;
    }
}
