package net.croz.nrich.notification.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Holder for notification data.
 */
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
public class Notification {

    /**
     * Title of the notification.
     */
    private final String title;

    /**
     * Content of the notification (i.e. for exception notification this will contain resolved message for exception or 'Error occurred' text).
     */
    private final String content;

    /**
     * List of messages (i.e. for validation failure notification this will contain all validation failure messages).
     */
    private final List<String> messageList;

    /**
     * Severity indicating importance of notification.
     */
    private final NotificationSeverity severity;

    /**
     * Additional ux notification options (i.e. autoHide etc.).
     */
    private final Map<String, ?> uxNotificationOptions;

}
