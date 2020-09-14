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

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.croz.nrich.notification.testutil.NotificationGeneratingUtil.additionalDataMap;
import static net.croz.nrich.notification.testutil.NotificationGeneratingUtil.invalidNotificationResolverServiceRequestBindingMap;
import static net.croz.nrich.notification.testutil.NotificationGeneratingUtil.invalidNotificationResolverServiceTestRequest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(NotificationTestConfiguration.class)
class DefaultNotificationResolverServiceTest {

    @Autowired
    private DefaultNotificationResolverService defaultNotificationResolverService;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Test
    void shouldConvertValidationFailureToNotificationErrorResponse() {
        // given
        final Map<String, Object> invalidBindingMap = invalidNotificationResolverServiceRequestBindingMap();
        final BindingResult result = validate(new NotificationResolverServiceTestRequest(), invalidBindingMap);

        // when
        final ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(result, NotificationResolverServiceTestRequest.class);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARNING);
        assertThat(notification.getTitle()).isNotBlank();

        assertThat(notification.getMessageList()).isNotEmpty();
        assertThat(notification.getMessageList()).containsExactlyInAnyOrder(
                "name: Name really cannot be null", "Last name: Size is not valid it has to be between: 1 and 5", "timestamp: Timestamp has to be in the future", "value: Minimum value for value field is: 10"
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
        final ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(result, null);

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
        final Map<String, Object> invalidBindingMap = new HashMap<>();

        final BindingResult result = validate(new NotificationResolverServiceTestRequest(), invalidBindingMap);
        result.addError(new ObjectError(NotificationResolverServiceTestRequest.class.getSimpleName(), "Object error occurred"));

        // when
        final ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(result, NotificationResolverServiceTestRequest.class);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARNING);
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
        final ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(result, NotificationResolverServiceTestWithoutMessageRequest.class);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getMessageList()).isNotEmpty();
        assertThat(notification.getMessageList()).containsExactlyInAnyOrder("name: must not be null");
    }

    @Test
    void shouldReturnDefinedExceptionMessageAndSeverity() {
        // given
        final Exception exception = new NotificationResolverServiceTestException();

        // when
        final Notification notification = defaultNotificationResolverService.createNotificationForException(exception);

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
        final Exception exception = new NotificationResolverServiceTestExceptionWithArguments("message");

        // when
        final Notification notification = defaultNotificationResolverService.createNotificationForException(exception, "1");

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getContent()).isEqualTo("Error message with arguments: 1");
    }

    @Test
    void shouldReturnDefaultMessageAndSeverityWhenNoneHasBeenDefined() {
        // given
        final Exception exception = new IllegalArgumentException();

        // when
        final Notification notification = defaultNotificationResolverService.createNotificationForException(exception);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.ERROR);
        assertThat(notification.getContent()).isNotEmpty();
    }

    @Test
    void shouldCreateSuccessfulNotification() {
        // given
        final String actionName = "upload.finished";

        // when
        final Notification notification = defaultNotificationResolverService.createNotificationForAction(actionName);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getTitle()).isNotBlank();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(notification.getContent()).isEqualTo("Upload finished");
    }

    @Test
    void shouldCreateValidationFailureNotificationWithCustomTitle() {
        final Map<String, Object> invalidBindingMap = invalidNotificationResolverServiceRequestBindingMap();

        final BindingResult result = validate(new NotificationResolverServiceTestRequestWithCustomTitle(), invalidBindingMap);

        // when
        final ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(result, NotificationResolverServiceTestRequestWithCustomTitle.class);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARNING);
        assertThat(notification.getTitle()).isEqualTo("Validation failure custom title");
    }

    @Test
    void shouldCreateErrorNotificationWithCustomTitle() {
        // given
        final Exception exception = new NotificationResolverServiceTestExceptionWithCustomTitle();

        // when
        final Notification notification = defaultNotificationResolverService.createNotificationForException(exception);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getTitle()).isEqualTo("Custom error title");
    }

    @Test
    void shouldCreateSuccessfulNotificationWithCustomTitle() {
        // given
        final String actionName = "upload.finished.with-title";

        // when
        final Notification notification = defaultNotificationResolverService.createNotificationForAction(actionName);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getTitle()).isEqualTo("Custom title");
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.INFO);
        assertThat(notification.getContent()).isEqualTo("Upload finished");
    }

    @Test
    void shouldAddAdditionalDataToValidationFailureNotification() {
        // given
        final Map<String, Object> additionalDataMap = additionalDataMap("data", "1");
        final Map<String, Object> invalidBindingMap = invalidNotificationResolverServiceRequestBindingMap();

        final BindingResult result = validate(new NotificationResolverServiceTestRequest(), invalidBindingMap);

        // when
        final ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(result, NotificationResolverServiceTestRequest.class, AdditionalNotificationData.builder().messageListDataMap(additionalDataMap).build());

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
        final Notification notification = defaultNotificationResolverService.createNotificationForException(exception, AdditionalNotificationData.builder().messageListDataMap(additionalDataMap).build());

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
        final Notification notification = defaultNotificationResolverService.createNotificationForAction(actionName, AdditionalNotificationData.builder().messageListDataMap(additionalDataMap).build());

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getMessageList()).containsExactly("Success: ok");
    }

    @Test
    void shouldIgnoreNotFoundAdditionalDataMessages() {
        // given
        final Map<String, Object> additionalDataMap = additionalDataMap("notFound", "ok");
        final String actionName = "upload.finished";

        // when
        final Notification notification = defaultNotificationResolverService.createNotificationForAction(actionName, AdditionalNotificationData.builder().messageListDataMap(additionalDataMap).build());

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getMessageList()).isEmpty();
    }

    @Test
    void shouldConvertConstraintViolationExceptionTooNotificationErrorResponse() {
        // given
        final NotificationResolverServiceTestRequest request = invalidNotificationResolverServiceTestRequest();
        final Set<ConstraintViolation<Object>> result = validate(request);

        // when
        final ValidationFailureNotification notification = defaultNotificationResolverService.createNotificationForValidationFailure(new ConstraintViolationException(result));

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getSeverity()).isEqualTo(NotificationSeverity.WARNING);
        assertThat(notification.getTitle()).isNotBlank();

        assertThat(notification.getMessageList()).isNotEmpty();
        assertThat(notification.getMessageList()).containsExactlyInAnyOrder(
                "name: Name really cannot be null", "Last name: Size is not valid it has to be between: 1 and 5", "timestamp: Timestamp has to be in the future", "value: Minimum value for value field is: 10"
        );
        assertThat(notification.getValidationErrorList()).isNotEmpty();
        assertThat(notification.getValidationErrorList().stream().map(ValidationError::getObjectName)).containsExactlyInAnyOrder("name", "lastName", "value", "timestamp");
    }

    private BindingResult validate(final Object objectToValidate, final Map<String, Object> valueMap) {
        final DataBinder binder = new DataBinder(objectToValidate);

        binder.setValidator(validator);

        binder.bind(new MutablePropertyValues(valueMap));

        binder.validate();

        return binder.getBindingResult();
    }

    private Set<ConstraintViolation<Object>> validate(final Object objectToValidate) {
        return validator.getValidator().validate(objectToValidate);
    }
}
