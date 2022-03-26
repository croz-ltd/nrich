package net.croz.nrich.registry.data.testutil;

import java.util.HashMap;
import java.util.Map;

public final class RegistryDataMapGeneratingUtil {

    private RegistryDataMapGeneratingUtil() {
    }

    public static Map<String, Class<?>> createFormConfigurationMap(String key, Class<?> value) {
        Map<String, Class<?>> formConfigurationMap = new HashMap<>();

        formConfigurationMap.put(key, value);

        return formConfigurationMap;
    }
}
