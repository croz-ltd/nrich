package net.croz.nrich.notification.service;

public final class NotificationResolverConstants {

    public static final String PREFIX_MESSAGE_FORMAT = "%s.%s";

    public static final String MESSAGE_TITLE_SUFFIX = "title";

    public static final String MESSAGE_SUFFIX = "message";

    public static final String MESSAGE_SEVERITY_SUFFIX = "severity";

    public static final String SUCCESS_MESSAGE_TITLE_CODE = "notification.success.title";

    public static final String SUCCESS_DEFAULT_CODE = "notification.success.default-message";

    public static final String SUCCESS_DEFAULT_TITLE = "Action has been executed";

    public static final String VALIDATION_FAILED_MESSAGE_TITLE_CODE = "notification.validation-failed.title";

    public static final String VALIDATION_FAILED_DEFAULT_TITLE = "Validation failed";

    public static final String ERROR_OCCURRED_MESSAGE_TITLE_CODE = "notification.error-occurred.title";

    public static final String ERROR_OCCURRED_DEFAULT_TITLE = "Error occurred";

    public static final String ERROR_OCCURRED_DEFAULT_CODE = "notification.error-occurred.default-message";

    private NotificationResolverConstants() {
    }
}
