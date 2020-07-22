package net.croz.nrich.notification.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Represents validation error on a object. Returned with {@link ValidationFailureNotification} instance.
 */
@RequiredArgsConstructor
@Getter
public class ValidationError {

    public static final String CONTAINING_OBJECT_NAME = "CONTAINING_OBJECT";

    /**
     * If validation error occurred on a property then name of a property otherwise <pre>CONTAINING_OBJECT</pre>.
     */
    private final String objectName;

    /**
     * List of validation failed messages.
     */
    private final List<String> errorMessageList;

}
