package net.croz.nrich.webmvc.advice.testutil;

import java.util.HashMap;
import java.util.Map;

public final class MapGeneratingUtil {

    private MapGeneratingUtil() {
    }

    public static Map<String, String> createMap(String firstKey, String firstValue, String secondKey, String secondValue) {
        Map<String, String> map = new HashMap<>();

        map.put(firstKey, firstValue);
        map.put(secondKey, secondValue);

        return map;
    }
}
