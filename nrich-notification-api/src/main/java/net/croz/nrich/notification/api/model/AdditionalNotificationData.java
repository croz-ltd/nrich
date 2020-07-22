package net.croz.nrich.notification.api.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Additional notification data used to configure notification.
 */
@Getter
@Builder
public class AdditionalNotificationData {

    /**
     * Overridden notification severity. If not specified notification will have severity depending on method that was used to generate (i.e NotificationSeverity.ERROR for createNotificationForException)
     */
    private final NotificationSeverity severity;

    /**
     * Additional data for which message will be resolved from {@link org.springframework.context.MessageSource} and added to Notification messageList.
     * Messages are resolved from key <pre>notification.additional-data.mapKey.message</pre> and value is passed in as argument when message resolving is performed.
     *
     */
    private final Map<String, ?> messageListDataMap;

    /**
     * Notification ux options. Can contain anything that client can interpret.
     *
     */
    private final Map<String, ?> uxNotificationOptions;

    public static AdditionalNotificationData empty() {
        return AdditionalNotificationData.builder().build();
    }
}
