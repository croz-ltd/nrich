package net.croz.nrich.formconfiguration.service;

import net.croz.nrich.formconfiguration.FormConfigurationTestConfiguration;
import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.stub.FormConfigurationServiceNestedTestRequest;
import net.croz.nrich.formconfiguration.stub.MessageSourceFieldErrorMessageResolverServiceTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static net.croz.nrich.formconfiguration.testutil.FormConfigurationGeneratingUtil.createConstrainedProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@SpringJUnitConfig(FormConfigurationTestConfiguration.class)
class MessageSourceFieldErrorMessageResolverServiceTest {

    @Autowired
    private MessageSourceFieldErrorMessageResolverService fieldErrorMessageResolverService;

    @Test
    void shouldResolveValidationMessageForConstrainedProperty() {
        // given
        ConstrainedProperty constrainedProperty = createConstrainedProperty(FormConfigurationServiceNestedTestRequest.class);

        // when
        String message = fieldErrorMessageResolverService.resolveErrorMessage(constrainedProperty, Locale.ENGLISH);

        // then
        assertThat(message).isEqualTo("Invalid value");
    }

    @Test
    void shouldConvertArraysToStringWhenResolvingMessage() {
        // given
        Map<String, Object> argumentMap = new HashMap<>();
        argumentMap.put("value", new String[] { "one", "two " });

        ConstrainedProperty constrainedProperty = createConstrainedProperty(MessageSourceFieldErrorMessageResolverServiceTestRequest.class, argumentMap);

        // when
        String message = fieldErrorMessageResolverService.resolveErrorMessage(constrainedProperty, Locale.ENGLISH);

        // then
        assertThat(message).isEqualTo("Not in list: one, two");
    }

    @Test
    void shouldNotFailOnNullConstraintArguments() {
        // given
        ConstrainedProperty constrainedProperty = createConstrainedProperty(MessageSourceFieldErrorMessageResolverServiceTestRequest.class);
        ConstrainedProperty constrainedPropertySpy = spy(constrainedProperty);

        doReturn(null).when(constrainedPropertySpy).getConstraintArgumentList();

        // when
        Throwable thrown = catchThrowable(() -> fieldErrorMessageResolverService.resolveErrorMessage(constrainedPropertySpy, Locale.ENGLISH));

        // then
        assertThat(thrown).isNull();
    }
}
