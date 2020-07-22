package net.croz.nrich.notification.api.model;

/**
 * Severity of the notification (i.e. for exceptions it is ERROR by default for informative messages like 'Entity has been saved' it is INFO etc).
 */
public enum NotificationSeverity {

    INFO, WARNING, ERROR

}
