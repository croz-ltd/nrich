package net.croz.nrich.notification.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ValidationFailureNotification extends Notification {

    private final List<ValidationError> validationErrorList;

    public ValidationFailureNotification(final String title, final List<String> messageList, final NotificationSeverity severity, final Map<String, ?> additionalNotificationData, final List<ValidationError> validationErrorList) {
        super(title, messageList, severity, additionalNotificationData);
        this.validationErrorList = validationErrorList;
    }
}
