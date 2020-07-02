package net.croz.nrich.notification.api.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class AdditionalNotificationData {

    private final NotificationSeverity severity;

    private final Map<String, ?> messageListData;

    private final Map<String, ?> uxNotificationOptions;

    public static AdditionalNotificationData empty() {
        return AdditionalNotificationData.builder().build();
    }
}
