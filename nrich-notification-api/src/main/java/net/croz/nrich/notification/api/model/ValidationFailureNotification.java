package net.croz.nrich.notification.api.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Holder for validation failure notification data. In addition to all data contained in regular notification it also contains a list of validation errors
 * (mapping between a property and validation failed message).
 */
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
public class ValidationFailureNotification extends Notification {

    private final List<ValidationError> validationErrorList;

    public ValidationFailureNotification(String title, String content, List<String> messageList, NotificationSeverity severity, Map<String, ?> uxNotificationOptions,
                                         List<ValidationError> validationErrorList) {
        super(title, content, messageList, severity, uxNotificationOptions);
        this.validationErrorList = validationErrorList;
    }
}
