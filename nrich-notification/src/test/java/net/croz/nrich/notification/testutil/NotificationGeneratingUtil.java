package net.croz.nrich.notification.testutil;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public final class NotificationGeneratingUtil {

    private NotificationGeneratingUtil() {
    }

    public static Map<String, Object> invalidNotificationResolverServiceRequestBindingMap() {
        final Map<String, Object> invalidBindingMap = new HashMap<>();

        invalidBindingMap.put("lastName", "too long last name");
        invalidBindingMap.put("timestamp", Instant.now().minus(1, ChronoUnit.DAYS));
        invalidBindingMap.put("value", 5);

        return invalidBindingMap;
    }

    public static Map<String, Object> additionalDataMap(final String key, final String value) {
        final Map<String, Object> additionalDataMap = new HashMap<>();

        additionalDataMap.put(key, value);

        return additionalDataMap;
    }
}