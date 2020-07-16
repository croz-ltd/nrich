package net.croz.nrich.notification.constant;

public final class NotificationConstants {

    public static final String PREFIX_MESSAGE_FORMAT = "%s.%s";

    public static final String CONSTRAINT_SHORT_MESSAGE_FORMAT = "%s.%s.%s";

    public static final String CONSTRAINT_FULL_MESSAGE_FORMAT = "%s.%s.%s.%s";

    public static final String FIELD_ERROR_FORMAT = "%s: %s";

    public static final String INVALID_SUFFIX = "invalid";

    public static final String FIELD_LABEL_SUFFIX = "label";

    public static final String MESSAGE_TITLE_SUFFIX = "title";

    public static final String MESSAGE_CONTENT_SUFFIX = "content";

    public static final String MESSAGE_SEVERITY_SUFFIX = "severity";

    public static final String SUCCESS_MESSAGE_TITLE_CODE = "notification.success.title";

    public static final String SUCCESS_DEFAULT_CODE = "notification.success.default-message";

    public static final String SUCCESS_DEFAULT_TITLE = "Action has been executed";

    public static final String VALIDATION_FAILED_MESSAGE_TITLE_CODE = "notification.validation-failed.title";

    public static final String VALIDATION_FAILED_CONTENT_CODE = "notification.validation-failed.content";

    public static final String VALIDATION_FAILED_DEFAULT_TITLE = "Validation failed";

    public static final String ERROR_OCCURRED_MESSAGE_TITLE_CODE = "notification.error-occurred.title";

    public static final String ERROR_OCCURRED_DEFAULT_TITLE = "Error occurred";

    public static final String ERROR_OCCURRED_DEFAULT_CODE = "notification.error-occurred.default-message";

    public static final String ADDITIONAL_EXCEPTION_DATA_MESSAGE_CODE_FORMAT = "notification.additional-data.%s.message";

    public static final String UNDEFINED_MESSAGE_VALUE = "UNDEFINED";

    public static final String UNKNOWN_VALIDATION_TARGET = "UNKNOWN_VALIDATION_TARGET";

    private NotificationConstants() {
    }
}
