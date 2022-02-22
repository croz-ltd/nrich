package net.croz.nrich.webmvc.advice;

import com.fasterxml.jackson.core.type.TypeReference;
import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.model.ValidationFailureNotification;
import net.croz.nrich.webmvc.test.BaseWebTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class NotificationErrorHandlingRestControllerAdviceTest extends BaseWebTest {

    private static final String DEFAULT_ERROR_MESSAGE = "Error message";

    private static final String NOTIFICATION_KEY = "notification";

    @Test
    void shouldResolveExceptionNotification() throws Exception {
        // when
        MockHttpServletResponse response = mockMvc.perform(post("/notificationErrorHandlingRestControllerAdviceTest/exceptionResolving").contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        String responseString = response.getContentAsString();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY.value());
        assertThat(responseString).isNotEmpty();

        // and when
        Map<String, Notification> convertedResponse = objectMapper.readValue(responseString, new TypeReference<Map<String, Notification>>() {
        });

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.get(NOTIFICATION_KEY)).isNotNull();

        // and when
        Notification notification = convertedResponse.get(NOTIFICATION_KEY);

        // then
        assertThat(notification.getContent()).isEqualTo(DEFAULT_ERROR_MESSAGE);
        assertThat(notification.getMessageList()).hasSize(1);
        assertThat(notification.getMessageList().get(0)).startsWith("UUID");
    }

    @Test
    void shouldResolveExceptionWithArgumentsNotification() throws Exception {
        // when
        String responseString = mockMvc.perform(post("/notificationErrorHandlingRestControllerAdviceTest/exceptionResolvingWithArguments").contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString();

        // then
        assertThat(responseString).isNotNull();

        // and when
        Map<String, Notification> convertedResponse = objectMapper.readValue(responseString, new TypeReference<Map<String, Notification>>() {
        });

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.get(NOTIFICATION_KEY)).isNotNull();

        // and when
        Notification notification = convertedResponse.get(NOTIFICATION_KEY);

        // then
        assertThat(notification.getContent()).isEqualTo("Error message with arguments: 1");
    }

    @Test
    void shouldResolveValidationNotification() throws Exception {
        // when
        MockHttpServletResponse response = mockMvc.perform(post("/notificationErrorHandlingRestControllerAdviceTest/validationFailedResolving").contentType(MediaType.APPLICATION_JSON).content("{}")).andReturn().getResponse();
        String responseString = response.getContentAsString();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseString).isNotEmpty();

        // and when
        Map<String, ValidationFailureNotification> convertedResponse = objectMapper.readValue(responseString, new TypeReference<Map<String, ValidationFailureNotification>>() {
        });

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.get(NOTIFICATION_KEY)).isNotNull();

        // and when
        ValidationFailureNotification notification = convertedResponse.get(NOTIFICATION_KEY);

        // then
        assertThat(notification.getContent()).isEqualTo("Found validation errors:");
        assertThat(notification.getMessageList()).contains("name: Name really cannot be null");
        assertThat(notification.getValidationErrorList()).isNotEmpty();
    }

    @Test
    void shouldResolveValidationBindFailedNotification() throws Exception {
        // when
        MockHttpServletResponse response = mockMvc.perform(post("/notificationErrorHandlingRestControllerAdviceTest/bindValidationFailedResolving")).andReturn().getResponse();
        String responseString = response.getContentAsString();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseString).isNotEmpty();

        // and when
        Map<String, ValidationFailureNotification> convertedResponse = objectMapper.readValue(responseString, new TypeReference<Map<String, ValidationFailureNotification>>() {
        });

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.get(NOTIFICATION_KEY)).isNotNull();

        // and when
        ValidationFailureNotification notification = convertedResponse.get(NOTIFICATION_KEY);

        // then
        assertThat(notification.getContent()).isEqualTo("Found validation errors:");
        assertThat(notification.getMessageList()).contains("name: Name really cannot be null");
        assertThat(notification.getValidationErrorList()).isNotEmpty();
    }

    @Test
    void shouldUnwrapException() throws Exception {
        // when
        MockHttpServletResponse response = mockMvc.perform(post("/notificationErrorHandlingRestControllerAdviceTest/unwrappedExceptionResolving")).andReturn().getResponse();
        String responseString = response.getContentAsString();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(responseString).isNotEmpty();

        // and when
        Map<String, Notification> convertedResponse = objectMapper.readValue(responseString, new TypeReference<Map<String, Notification>>() {
        });

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.get(NOTIFICATION_KEY)).isNotNull();

        // and when
        Notification notification = convertedResponse.get(NOTIFICATION_KEY);

        // then
        assertThat(notification.getContent()).isEqualTo(DEFAULT_ERROR_MESSAGE);
    }

    @Test
    void shouldUnwrapValidationException() throws Exception {
        // when
        MockHttpServletResponse response = mockMvc.perform(post("/notificationErrorHandlingRestControllerAdviceTest/unwrappedExceptionValidationFailedResolving")).andReturn().getResponse();
        String responseString = response.getContentAsString();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseString).isNotEmpty();
    }

    @Test
    void shouldUnwrapBindException() throws Exception {
        // when
        MockHttpServletResponse response = mockMvc.perform(post("/notificationErrorHandlingRestControllerAdviceTest/unwrappedExceptionBindExceptionResolving")).andReturn().getResponse();
        String responseString = response.getContentAsString();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseString).isNotEmpty();
    }

    @Test
    void shouldResolveConstraintExceptionNotification() throws Exception {
        // when
        MockHttpServletResponse response = mockMvc.perform(post("/notificationErrorHandlingRestControllerAdviceTest/constraintViolationExceptionResolving").contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
        String responseString = response.getContentAsString();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(responseString).isNotEmpty();

        // and when
        Map<String, ValidationFailureNotification> convertedResponse = objectMapper.readValue(responseString, new TypeReference<Map<String, ValidationFailureNotification>>() {
        });

        // then
        assertThat(convertedResponse).isNotNull();
        assertThat(convertedResponse.get(NOTIFICATION_KEY)).isNotNull();

        // and when
        ValidationFailureNotification notification = convertedResponse.get(NOTIFICATION_KEY);

        // then
        assertThat(notification.getContent()).isEqualTo("Found validation errors:");
        assertThat(notification.getMessageList()).contains("name: Name really cannot be null");
        assertThat(notification.getValidationErrorList()).isNotEmpty();
    }

}
