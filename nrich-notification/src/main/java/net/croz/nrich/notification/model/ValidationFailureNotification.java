package net.croz.nrich.notification.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Data
public class ValidationFailureNotification extends Notification {

    private final List<ValidationError> validationErrorList;

    public ValidationFailureNotification(final String title, final String contentText, final List<String> messageList, final NotificationSeverity severity, final List<ValidationError> validationErrorList) {
        super(title, contentText, messageList, severity);
        this.validationErrorList = validationErrorList;
    }
}
