package net.croz.nrich.notification.service.impl;

import net.croz.nrich.notification.NotificationTestConfiguration;
import net.croz.nrich.notification.model.Notification;
import net.croz.nrich.notification.model.NotificationSeverity;
import net.croz.nrich.notification.model.ValidationError;
import net.croz.nrich.notification.model.ValidationFailureNotification;
import net.croz.nrich.notification.stub.NotificationResolverServiceTestException;
import net.croz.nrich.notification.stub.NotificationResolverServiceTestExceptionWithArguments;
import net.croz.nrich.notification.stub.NotificationResolverServiceTestExceptionWithCustomTitle;
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
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.Map;

import static net.croz.nrich.notification.testutil.NotificationGeneratingUtil.additionalDataMap;
import static net.croz.nrich.notification.testutil.NotificationGeneratingUtil.invalidNotificationResolverServiceRequestBindingMap;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = NotificationTestConfiguration.class)
public class NotificationResolverServiceImplTest {

    @Autowired
    private NotificationResolverServiceImpl notificationResolverServiceImpl;

    @Autowired
    private Validator validator;

    @Test
    void shouldConvertValidationFailureToNotificationErrorResponse() {
        // given
        final Map<String, Object> invalidBindingMap = invalidNotificationResolverServiceRequestBindingMap();
        final BindingResult result = validate(new NotificationResolverServiceTestRequest(), invalidBindingMap);

        // when
        final ValidationFailureNotification notification = notificationResolverServiceImpl.createMessageNotificationForValidationFailure(result, NotificationResolverServiceTestRequest.class);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARN);
        assertThat(notification.getTitle()).isNotBlank();

        assertThat(notification.getMessageList()).isNotEmpty();
        assertThat(notification.getMessageList()).containsExactlyInAnyOrder(
                "Name really cannot be null", "Size is not valid it has to be between: 1 and 5", "Timestamp has to be in the future", "Minimum value for value field is: 10"
        );
        assertThat(notification.getValidationErrorList()).isNotEmpty();
        assertThat(notification.getValidationErrorList().stream().map(ValidationError::getObjectName)).containsExactlyInAnyOrder("name", "lastName", "value", "timestamp");
    }

    @Test
    void shouldNotFailConversionWhenOwningTypeIsNull() {
        // given
        final Map<String, Object> invalidBindingMap = new HashMap<>();

        final BindingResult result = validate(new NotificationResolverServiceTestRequest(), invalidBindingMap);

        // when
        final ValidationFailureNotification notification = notificationResolverServiceImpl.createMessageNotificationForValidationFailure(result, null);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARN);
        assertThat(notification.getTitle()).isNotBlank();

        assertThat(notification.getMessageList()).isNotEmpty();
        assertThat(notification.getValidationErrorList()).isNotEmpty();
    }

    @Test
    void shouldConvertObjectErrorsToValidationMessages() {
        // given
        final Map<String, Object> invalidBindingMap = new HashMap<>();

        final BindingResult result = validate(new NotificationResolverServiceTestRequest(), invalidBindingMap);
        result.addError(new ObjectError(NotificationResolverServiceTestRequest.class.getSimpleName(), "Object error occurred"));

        // when
        final ValidationFailureNotification notification = notificationResolverServiceImpl.createMessageNotificationForValidationFailure(result, NotificationResolverServiceTestRequest.class);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARN);
        assertThat(notification.getTitle()).isNotBlank();

        assertThat(notification.getMessageList()).isNotEmpty();
        assertThat(notification.getMessageList()).contains("Object error occurred");

        assertThat(notification.getValidationErrorList()).isNotEmpty();
        assertThat(notification.getValidationErrorList().stream().map(ValidationError::getObjectName)).contains(ValidationError.CONTAINING_OBJECT_NAME);
    }

    @Test
    void shouldReturnDefaultErrorWhenThereIsNoMessageDefined() {
        // given
        final BindingResult result = validate(new NotificationResolverServiceTestWithoutMessageRequest(), new HashMap<>());

        // when
        final ValidationFailureNotification notification = notificationResolverServiceImpl.createMessageNotificationForValidationFailure(result, NotificationResolverServiceTestWithoutMessageRequest.class);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getMessageList()).isNotEmpty();
        assertThat(notification.getMessageList()).containsExactlyInAnyOrder("must not be null");
    }

    @Test
    void shouldReturnDefinedExceptionMessageAndSeverity() {
        // given
        final Exception exception = new NotificationResolverServiceTestException();

        // when
        final Notification notification = notificationResolverServiceImpl.createNotificationForException(exception);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getTitle()).isNotBlank();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARN);
        assertThat(notification.getContentText()).isEqualTo("Error message");
        assertThat(notification.getMessageList()).isEmpty();
    }

    @Test
    void shouldAddMessageArgumentsForDefinedExceptions() {
        // given
        final Exception exception = new NotificationResolverServiceTestExceptionWithArguments("message");

        // when
        final Notification notification = notificationResolverServiceImpl.createNotificationForException(exception, "1");

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getContentText()).isEqualTo("Error message with arguments: 1");
    }

    @Test
    void shouldReturnDefaultMessageAndSeverityWhenNoneHasBeenDefined() {
        // given
        final Exception exception = new IllegalArgumentException();

        // when
        final Notification notification = notificationResolverServiceImpl.createNotificationForException(exception);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.ERROR);
        assertThat(notification.getContentText()).isNotEmpty();
    }

    @Test
    void shouldCreateSuccessfulNotification() {
        // given
        final String actionName = "upload.finished";

        // when
        final Notification notification = notificationResolverServiceImpl.createNotificationForSuccessfulAction(actionName);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getTitle()).isNotBlank();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(notification.getContentText()).isEqualTo("Upload finished");
    }

    @Test
    void shouldCreateValidationFailureNotificationWithCustomTitle() {
        final Map<String, Object> invalidBindingMap = invalidNotificationResolverServiceRequestBindingMap();

        final BindingResult result = validate(new NotificationResolverServiceTestRequestWithCustomTitle(), invalidBindingMap);

        // when
        final ValidationFailureNotification notification = notificationResolverServiceImpl.createMessageNotificationForValidationFailure(result, NotificationResolverServiceTestRequestWithCustomTitle.class);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARN);
        assertThat(notification.getTitle()).isEqualTo("Validation failure custom title");
    }

    @Test
    void shouldCreateErrorNotificationWithCustomTitle() {
        // given
        final Exception exception = new NotificationResolverServiceTestExceptionWithCustomTitle();

        // when
        final Notification notification = notificationResolverServiceImpl.createNotificationForException(exception);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getTitle()).isEqualTo("Custom error title");
    }

    @Test
    void shouldCreateSuccessfulNotificationWithCustomTitle() {
        // given
        final String actionName = "upload.finished.with-title";

        // when
        final Notification notification = notificationResolverServiceImpl.createNotificationForSuccessfulAction(actionName);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getTitle()).isEqualTo("Custom title");
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(notification.getContentText()).isEqualTo("Upload finished");
    }

    @Test
    void shouldAddAdditionalDataToValidationFailureNotification() {
        // given
        final Map<String, Object> additionalDataMap = additionalDataMap("data", "1");
        final Map<String, Object> invalidBindingMap = invalidNotificationResolverServiceRequestBindingMap();

        final BindingResult result = validate(new NotificationResolverServiceTestRequest(), invalidBindingMap);

        // when
        final ValidationFailureNotification notification = notificationResolverServiceImpl.createMessageNotificationForValidationFailure(result, NotificationResolverServiceTestRequest.class, additionalDataMap);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getMessageList()).contains("Data: 1");
    }

    @Test
    void shouldAddAdditionalDataToExceptionNotification() {
        // given
        final Map<String, Object> additionalDataMap = additionalDataMap("data", "1");
        final Exception exception = new NotificationResolverServiceTestExceptionWithCustomTitle();

        // when
        final Notification notification = notificationResolverServiceImpl.createNotificationForException(exception, additionalDataMap);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getMessageList()).containsExactly("Data: 1");
    }

    @Test
    void shouldAddAdditionalDataToSuccessNotification() {
        // given
        final Map<String, Object> additionalDataMap = additionalDataMap("success", "ok");
        final String actionName = "upload.finished";

        // when
        final Notification notification = notificationResolverServiceImpl.createNotificationForSuccessfulAction(actionName, additionalDataMap);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getMessageList()).containsExactly("Success: ok");
    }

    private BindingResult validate(final Object objectToValidate, final Map<String, Object> valueMap) {
        final DataBinder binder = new DataBinder(objectToValidate);

        binder.setValidator(validator);

        binder.bind(new MutablePropertyValues(valueMap));

        binder.validate();

        return binder.getBindingResult();
    }
}
