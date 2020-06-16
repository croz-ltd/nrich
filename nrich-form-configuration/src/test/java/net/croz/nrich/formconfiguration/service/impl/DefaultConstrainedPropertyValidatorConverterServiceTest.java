package net.croz.nrich.formconfiguration.service.impl;

import net.croz.nrich.formconfiguration.FormConfigurationTestConfiguration;
import net.croz.nrich.formconfiguration.api.model.ConstrainedProperty;
import net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration;
import net.croz.nrich.formconfiguration.stub.FormConfigurationServiceTestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static net.croz.nrich.formconfiguration.testutil.FormConfigurationGeneratingUtil.createConstrainedProperty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringJUnitConfig(FormConfigurationTestConfiguration.class)
public class DefaultConstrainedPropertyValidatorConverterServiceTest {

    @Autowired
    private DefaultConstrainedPropertyValidatorConverterService defaultConstrainedPropertyValidatorConverterService;

    @Test
    void shouldConvertConstrainedPropertyToValidatorConfiguration() {
        // given
        final ConstrainedProperty constrainedProperty = createConstrainedProperty(FormConfigurationServiceTestRequest.class);

        // when
        final List<ConstrainedPropertyClientValidatorConfiguration> validationConfigurationList = defaultConstrainedPropertyValidatorConverterService.convert(constrainedProperty);

        // then
        assertThat(validationConfigurationList).hasSize(1);

        // and when
        final ConstrainedPropertyClientValidatorConfiguration validatorConfiguration = validationConfigurationList.get(0);

        // then
        assertThat(validatorConfiguration).isNotNull();
        assertThat(validatorConfiguration.getArgumentList()).isEmpty();
        assertThat(validatorConfiguration.getName()).isEqualTo("NotNull");
        assertThat(validatorConfiguration.getErrorMessage()).isEqualTo("Really cannot be null");
    }

    @Test
    void shouldSupportAll() {
        // given
        final ConstrainedProperty property = mock(ConstrainedProperty.class);

        // when
        final boolean result = defaultConstrainedPropertyValidatorConverterService.supports(property);

        // then
        assertThat(result).isTrue();
    }
}
