/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.notification.api.service;

import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.model.ValidationFailureNotification;
import org.springframework.validation.Errors;

import jakarta.validation.ConstraintViolationException;

/**
 * Creates notifications for exceptions, validation failures and actions.
 */
public interface NotificationResolverService {

    /**
     * Returns {@link ValidationFailureNotification} instance. Default severity is <pre>WARNING</pre>. Resolved validation failure messages are added to notification messageList.
     * Title of notification is resolved from code <pre>notification.validation-failed.title</pre> and content of notification is resolved from code: <pre>notification.validation-failed.content</pre>.
     *
     * @param errors                     Spring's {@link Errors} that will be used to resolve validation notification messages.
     * @param validationFailedOwningType class on which validation errors were found
     * @param additionalNotificationData additional notification data to add to notification
     * @return {@link ValidationFailureNotification} instance
     */
    ValidationFailureNotification createNotificationForValidationFailure(Errors errors, Class<?> validationFailedOwningType, AdditionalNotificationData additionalNotificationData);

    /**
     * Returns {@link ValidationFailureNotification} instance. Default severity is <pre>WARNING</pre>. Resolved validation failure messages are added to notification messageList.
     * Title of notification is resolved from code <pre>notification.validation-failed.title</pre> and content of notification is resolved from code: <pre>notification.validation-failed.content</pre>.
     *
     * @param exception                  validation exception that will be used to resolve validation notification messages.
     * @param additionalNotificationData additional notification data to add to notification
     * @return {@link ValidationFailureNotification} instance
     */
    ValidationFailureNotification createNotificationForValidationFailure(ConstraintViolationException exception, AdditionalNotificationData additionalNotificationData);

    /**
     * Returns {@link Notification} instance for exception. Default severity is <pre>ERROR</pre>. Resolved error message is added as notification content.
     * Title of notification is resolved from code <pre>fullyQualifiedExceptionClass.title</pre> and content of notification is resolved from code: <pre>fullyQualifiedExceptionClass.code</pre>.
     *
     * @param throwable                    exception for which to resolve notification
     * @param additionalNotificationData   additional notification data to add to notification
     * @param exceptionMessageArgumentList optional exception argument list, will be used when resolving exception message
     * @return {@link Notification} instance
     */
    Notification createNotificationForException(Throwable throwable, AdditionalNotificationData additionalNotificationData, Object... exceptionMessageArgumentList);

    /**
     * Returns {@link Notification} instance for action. Default severity is <pre>INFO</pre>. Resolved action message is added as notification content.
     * Title of notification is resolved from code <pre>actionName.title</pre> and content of notification is resolved from code: <pre>actionName.code</pre>.
     *
     * @param actionName                 name of the action for which to resolve notification
     * @param additionalNotificationData additional notification data to add to notification
     * @return {@link Notification} instance
     */
    Notification createNotificationForAction(String actionName, AdditionalNotificationData additionalNotificationData);

    default ValidationFailureNotification createNotificationForValidationFailure(Errors errors, Class<?> validationFailedOwningType) {
        return createNotificationForValidationFailure(errors, validationFailedOwningType, AdditionalNotificationData.empty());
    }

    default ValidationFailureNotification createNotificationForValidationFailure(ConstraintViolationException exception) {
        return createNotificationForValidationFailure(exception, AdditionalNotificationData.empty());
    }

    default Notification createNotificationForException(Throwable throwable, Object... exceptionMessageArgumentList) {
        return createNotificationForException(throwable, AdditionalNotificationData.empty(), exceptionMessageArgumentList);
    }

    default Notification createNotificationForAction(String actionName) {
        return createNotificationForAction(actionName, AdditionalNotificationData.empty());
    }
}
