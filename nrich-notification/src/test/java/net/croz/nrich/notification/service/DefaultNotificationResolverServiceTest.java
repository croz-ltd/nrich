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

package net.croz.nrich.notification.service;

import net.croz.nrich.notification.NotificationTestConfiguration;
import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.model.NotificationSeverity;
import net.croz.nrich.notification.api.model.ValidationError;
import net.croz.nrich.notification.api.model.ValidationFailureNotification;
import net.croz.nrich.notification.stub.NotificationResolverServiceTestException;
import net.croz.nrich.notification.stub.NotificationResolverServiceTestExceptionWithArguments;
import net.croz.nrich.notification.stub.NotificationResolverServiceTestExceptionWithCustomTitle;
import net.croz.nrich.notification.stub.NotificationResolverServiceTestExceptionWithMessage;
import net.croz.nrich.notification.stub.NotificationResolverServiceTestExceptionWithMessageCode;
import net.croz.nrich.notification.stub.NotificationResolverServiceTestRequest;
import net.croz.nrich.notification.stub.NotificationResolverServiceTestRequestWithCustomTitle;
import net.croz.nrich.notification.stub.NotificationResolverServiceTestWithoutMessageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.croz.nrich.notification.testutil.NotificationGeneratingUtil.invalidNotificationResolverServiceRequestBindingMap;
import static net.croz.nrich.notification.testutil.NotificationGeneratingUtil.invalidNotificationResolverServiceTestRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(NotificationTestConfiguration.class)
class DefaultNotificationResolverServiceTest {

    @Autowired
    private DefaultNotificationResolverService defaultNotificationResolverService;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Test
    void shouldConvertValidationFailureToNotificationErrorResponse() {
        // given
        Map<String, Object> invalidBindingMap = invalidNotificationResolverServiceRequestBindingMap();
        BindingResult result = validate(new NotificationResolverServiceTestRequest(), invalidBindingMap);

        // when
        ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(result, NotificationResolverServiceTestRequest.class);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARNING);
        assertThat(notification.getTitle()).isNotBlank();

        assertThat(notification.getMessageList()).isNotEmpty();
        assertThat(notification.getMessageList()).containsExactlyInAnyOrder(
            "Name description: Name really cannot be null", "Last name: Size is not valid it has to be between: 1 and 5",
            "timestamp: Timestamp has to be in the future", "value: Minimum value for value field is: 10"
        );
        assertThat(notification.getValidationErrorList()).isNotEmpty();
        assertThat(notification.getValidationErrorList().stream().map(ValidationError::objectName)).containsExactlyInAnyOrder("name", "lastName", "value", "timestamp");
    }

    @Test
    void shouldNotFailConversionWhenOwningTypeIsNull() {
        // given
        Map<String, Object> invalidBindingMap = new HashMap<>();

        BindingResult result = validate(new NotificationResolverServiceTestRequest(), invalidBindingMap);

        // when
        ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(result, null);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARNING);
        assertThat(notification.getTitle()).isNotBlank();

        assertThat(notification.getMessageList()).isNotEmpty();
        assertThat(notification.getValidationErrorList()).isNotEmpty();
    }

    @Test
    void shouldConvertObjectErrorsToValidationMessages() {
        // given
        Map<String, Object> invalidBindingMap = new HashMap<>();

        BindingResult result = validate(new NotificationResolverServiceTestRequest(), invalidBindingMap);
        result.addError(new ObjectError(NotificationResolverServiceTestRequest.class.getSimpleName(), "Object error occurred"));

        // when
        ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(result, NotificationResolverServiceTestRequest.class);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARNING);
        assertThat(notification.getTitle()).isNotBlank();

        assertThat(notification.getMessageList()).isNotEmpty();
        assertThat(notification.getMessageList()).contains("Object error occurred");

        assertThat(notification.getValidationErrorList()).isNotEmpty();
        assertThat(notification.getValidationErrorList().stream().map(ValidationError::objectName)).contains(ValidationError.CONTAINING_OBJECT_NAME);
    }

    @Test
    void shouldReturnDefaultErrorWhenThereIsNoMessageDefined() {
        // given
        BindingResult result = validate(new NotificationResolverServiceTestWithoutMessageRequest(), new HashMap<>());

        // when
        ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(result, NotificationResolverServiceTestWithoutMessageRequest.class);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getMessageList()).isNotEmpty();
        assertThat(notification.getMessageList()).containsExactlyInAnyOrder("Name description: must not be null");
    }

    @Test
    void shouldReturnDefinedExceptionMessageAndSeverity() {
        // given
        Exception exception = new NotificationResolverServiceTestException();

        // when
        Notification notification = defaultNotificationResolverService.createNotificationForException(exception);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getTitle()).isNotBlank();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARNING);
        assertThat(notification.getContent()).isEqualTo("Error message");
        assertThat(notification.getMessageList()).isEmpty();
    }

    @Test
    void shouldAddMessageArgumentsForDefinedExceptions() {
        // given
        Exception exception = new NotificationResolverServiceTestExceptionWithArguments("message", "1");

        // when
        Notification notification = defaultNotificationResolverService.createNotificationForException(exception);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getContent()).isEqualTo("Error message with arguments: 1");
    }

    @Test
    void shouldReturnDefaultMessageAndSeverityWhenNoneHasBeenDefined() {
        // given
        Exception exception = new IllegalArgumentException();

        // when
        Notification notification = defaultNotificationResolverService.createNotificationForException(exception);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.ERROR);
        assertThat(notification.getContent()).isNotEmpty();
    }

    @Test
    void shouldCreateSuccessfulNotification() {
        // given
        String actionName = "upload.finished";

        // when
        Notification notification = defaultNotificationResolverService.createNotificationForAction(actionName);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getTitle()).isNotBlank();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(notification.getContent()).isEqualTo("Upload finished");
    }

    @Test
    void shouldCreateValidationFailureNotificationWithCustomTitle() {
        Map<String, Object> invalidBindingMap = invalidNotificationResolverServiceRequestBindingMap();

        BindingResult result = validate(new NotificationResolverServiceTestRequestWithCustomTitle(), invalidBindingMap);

        // when
        ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(result, NotificationResolverServiceTestRequestWithCustomTitle.class);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARNING);
        assertThat(notification.getTitle()).isEqualTo("Validation failure custom title");
    }

    @Test
    void shouldCreateErrorNotificationWithCustomTitle() {
        // given
        Exception exception = new NotificationResolverServiceTestExceptionWithCustomTitle();

        // when
        Notification notification = defaultNotificationResolverService.createNotificationForException(exception);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getTitle()).isEqualTo("Custom error title");
    }

    @Test
    void shouldCreateSuccessfulNotificationWithCustomTitle() {
        // given
        String actionName = "upload.finished.with-title";

        // when
        Notification notification = defaultNotificationResolverService.createNotificationForAction(actionName);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getTitle()).isEqualTo("Custom title");
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(notification.getContent()).isEqualTo("Upload finished");
    }

    @Test
    void shouldAddAdditionalDataToValidationFailureNotification() {
        // given
        AdditionalNotificationData notificationData = AdditionalNotificationData.builder().messageListDataMap(Map.of("data", "1")).build();
        Map<String, Object> invalidBindingMap = invalidNotificationResolverServiceRequestBindingMap();

        BindingResult result = validate(new NotificationResolverServiceTestRequest(), invalidBindingMap);

        // when
        ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(result, NotificationResolverServiceTestRequest.class, notificationData);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getMessageList()).contains("Data: 1");
    }

    @Test
    void shouldAddAdditionalDataToExceptionNotification() {
        // given
        AdditionalNotificationData notificationData = AdditionalNotificationData.builder().messageListDataMap(Map.of("data", "1")).build();
        Exception exception = new NotificationResolverServiceTestExceptionWithCustomTitle();

        // when
        Notification notification = defaultNotificationResolverService.createNotificationForException(exception, notificationData);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getMessageList()).containsExactly("Data: 1");
    }

    @Test
    void shouldAddAdditionalDataToSuccessNotification() {
        // given
        AdditionalNotificationData notificationData = AdditionalNotificationData.builder().messageListDataMap(Map.of("success", "ok")).build();
        String actionName = "upload.finished";

        // when
        Notification notification = defaultNotificationResolverService.createNotificationForAction(actionName, notificationData);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getMessageList()).containsExactly("Success: ok");
    }

    @Test
    void shouldIgnoreNotFoundAdditionalDataMessages() {
        // given
        AdditionalNotificationData notificationData = AdditionalNotificationData.builder().messageListDataMap(Map.of("notFound", "ok")).build();
        String actionName = "upload.finished";

        // when
        Notification notification = defaultNotificationResolverService.createNotificationForAction(actionName, notificationData);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getMessageList()).isEmpty();
    }

    @Test
    void shouldConvertConstraintViolationExceptionTooNotificationErrorResponse() {
        // given
        NotificationResolverServiceTestRequest request = invalidNotificationResolverServiceTestRequest();
        Set<ConstraintViolation<Object>> result = validate(request);

        // when
        ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(new ConstraintViolationException(result));

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARNING);
        assertThat(notification.getTitle()).isNotBlank();

        assertThat(notification.getMessageList()).isNotEmpty();
        assertThat(notification.getMessageList()).containsExactlyInAnyOrder(
            "Name description: Name really cannot be null", "Last name: Size is not valid it has to be between: 1 and 5",
            "timestamp: Timestamp has to be in the future", "value: Minimum value for value field is: 10", "elementList[1]: size must be between 4 and 20"
        );
        assertThat(notification.getValidationErrorList()).isNotEmpty();
        assertThat(notification.getValidationErrorList().stream().map(ValidationError::objectName)).containsExactlyInAnyOrder("name", "lastName", "value", "timestamp", "elementList[1]");
    }

    @Test
    void shouldNotFailWithNullArguments() {
        // given
        AdditionalNotificationData notificationData = AdditionalNotificationData.builder().build();
        Exception exception = new NotificationResolverServiceTestExceptionWithArguments("", (Object[]) null);

        // when
        Throwable thrown = catchThrowable(() -> defaultNotificationResolverService.createNotificationForException(exception, notificationData));

        // then
        assertThat(thrown).isNull();
    }

    @Test
    void shouldResolveNotificationContentFromProvidedMessageCode() {
        // given
        Exception exception = new NotificationResolverServiceTestExceptionWithMessageCode();

        // when
        Notification notification = defaultNotificationResolverService.createNotificationForException(exception);

        // then
        assertThat(notification.getContent()).isEqualTo("Content resolved from message code");
    }

    @Test
    void shouldResolveNotificationContentFromProvidedMessage() {
        // given
        String messageContent = "Message content";
        Exception exception = new NotificationResolverServiceTestExceptionWithMessage(messageContent);

        // when
        Notification notification = defaultNotificationResolverService.createNotificationForException(exception);

        // then
        assertThat(notification.getContent()).isEqualTo(messageContent);
    }

    private BindingResult validate(Object objectToValidate, Map<String, Object> valueMap) {
        DataBinder binder = new DataBinder(objectToValidate);

        binder.setValidator(validator);

        binder.bind(new MutablePropertyValues(valueMap));

        binder.validate();

        return binder.getBindingResult();
    }

    private Set<ConstraintViolation<Object>> validate(Object objectToValidate) {
        return validator.getValidator().validate(objectToValidate);
    }
}
