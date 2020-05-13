package net.croz.nrich.notification.model;

import lombok.Data;

import java.util.List;

@Data
public class ValidationFailureNotification extends Notification {

    private final List<ValidationError> validationErrorList;

    public ValidationFailureNotification(final String title, final String contentText, final List<String> messageList, final NotificationSeverity severity, final List<ValidationError> validationErrorList) {
        super(title, contentText, messageList, severity);
        this.validationErrorList = validationErrorList;
    }
}
