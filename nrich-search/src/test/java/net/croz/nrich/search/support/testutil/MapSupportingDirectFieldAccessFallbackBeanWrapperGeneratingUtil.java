package net.croz.nrich.search.support.testutil;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public final class MapSupportingDirectFieldAccessFallbackBeanWrapperGeneratingUtil {

    private MapSupportingDirectFieldAccessFallbackBeanWrapperGeneratingUtil() {
    }

    public static Map<String, Object> mapOf(final String key, final Object value) {
        final Map<String, Object> map = new HashMap<>();

        map.put(key, value);

        return map;
    }

    public static Object entityWithPropertyValue(final Object value) {
        return new Entity(value);
    }

    @Data
    static class Entity {

        private final Object name;

    }
}
