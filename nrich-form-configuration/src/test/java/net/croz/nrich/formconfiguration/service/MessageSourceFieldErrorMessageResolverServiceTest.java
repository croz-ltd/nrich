package net.croz.nrich.formconfiguration.service;

import net.croz.nrich.formconfiguration.FormConfigurationTestConfiguration;
import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.stub.FormConfigurationServiceNestedTestRequest;
import net.croz.nrich.formconfiguration.stub.MessageSourceFieldErrorMessageResolverServiceTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static net.croz.nrich.formconfiguration.testutil.FormConfigurationGeneratingUtil.createConstrainedProperty;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(FormConfigurationTestConfiguration.class)
public class MessageSourceFieldErrorMessageResolverServiceTest {

    @Autowired
    private MessageSourceFieldErrorMessageResolverService fieldErrorMessageResolverService;

    @Test
    void shouldResolveValidationMessageForConstrainedProperty() {
        // given
        final ConstrainedProperty constrainedProperty = createConstrainedProperty(FormConfigurationServiceNestedTestRequest.class);

        // when
        final String message = fieldErrorMessageResolverService.resolveErrorMessage(constrainedProperty, new Locale("en"));

        // then
        assertThat(message).isEqualTo("Invalid value");
    }

    @Test
    void shouldConvertArraysToStringWhenResolvingMessage() {
        // given
        final Map<String, Object> argumentMap = new HashMap<>();
        argumentMap.put("value", new String[] { "one", "two "});

        final ConstrainedProperty constrainedProperty = createConstrainedProperty(MessageSourceFieldErrorMessageResolverServiceTestRequest.class, argumentMap);

        // when
        final String message = fieldErrorMessageResolverService.resolveErrorMessage(constrainedProperty, new Locale("en"));

        // then
        assertThat(message).isEqualTo("Not in list: one, two");
    }
}
