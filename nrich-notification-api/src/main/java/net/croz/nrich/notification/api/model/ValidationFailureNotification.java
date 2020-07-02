package net.croz.nrich.notification.api.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
public class ValidationFailureNotification extends Notification {

    private final List<ValidationError> validationErrorList;

    public ValidationFailureNotification(final String title, final String contentText, final List<String> messageList, final NotificationSeverity severity, final Map<String, ?> uxNotificationOptions, final List<ValidationError> validationErrorList) {
        super(title, contentText, messageList, severity, uxNotificationOptions);
        this.validationErrorList = validationErrorList;
    }
}
