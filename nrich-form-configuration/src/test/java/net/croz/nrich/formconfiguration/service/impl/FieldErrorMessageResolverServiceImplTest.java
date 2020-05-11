package net.croz.nrich.formconfiguration.service.impl;

import net.croz.nrich.formconfiguration.FormConfigurationTestConfiguration;
import net.croz.nrich.formconfiguration.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.stub.FormConfigurationServiceNestedTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Locale;

import static net.croz.nrich.formconfiguration.testutil.FormConfigurationGeneratingUtil.createConstrainedProperty;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(classes = FormConfigurationTestConfiguration.class)
public class FieldErrorMessageResolverServiceImplTest {

    @Autowired
    private FieldErrorMessageResolverServiceImpl fieldErrorMessageResolverService;

    @Test
    void shouldResolveValidationMessageForConstrainedProperty() {
        // given
        final ConstrainedProperty constrainedProperty = createConstrainedProperty(FormConfigurationServiceNestedTestRequest.class);

        // when
        final String message = fieldErrorMessageResolverService.resolveErrorMessage(constrainedProperty, new Locale("en"));

        // then
        assertThat(message).isEqualTo("Invalid value");
    }
}
