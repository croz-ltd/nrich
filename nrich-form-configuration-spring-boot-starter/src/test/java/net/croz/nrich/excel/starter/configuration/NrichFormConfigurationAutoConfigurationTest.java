package net.croz.nrich.excel.starter.configuration;

import net.croz.nrich.formconfiguration.api.service.ConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationService;
import net.croz.nrich.formconfiguration.controller.FormConfigurationController;
import net.croz.nrich.formconfiguration.service.FieldErrorMessageResolverService;
import net.croz.nrich.formconfiguration.starter.configuration.NrichFormConfigurationAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;

class NrichFormConfigurationAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichFormConfigurationAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        contextRunner.withBean(LocalValidatorFactoryBean.class).run(context -> {
            assertThat(context).hasSingleBean(FieldErrorMessageResolverService.class);
            assertThat(context).hasSingleBean(FormConfigurationService.class);
            assertThat(context).hasSingleBean(FormConfigurationController.class);
            assertThat(context).hasBean(NrichFormConfigurationAutoConfiguration.FORM_CONFIGURATION_MAPPING_BEAN_NAME);
        });
    }

    @Test
    void shouldNotConfigureWhenNoValidatorBeanIsPresent() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(FieldErrorMessageResolverService.class);
            assertThat(context).doesNotHaveBean(FormConfigurationService.class);
            assertThat(context).doesNotHaveBean(FormConfigurationController.class);
            assertThat(context).doesNotHaveBean(NrichFormConfigurationAutoConfiguration.FORM_CONFIGURATION_MAPPING_BEAN_NAME);
        });
    }

    @Test
    void shouldNotCreateDefaultValueConverterWhenCreationIsDisabled() {
        contextRunner.withBean(LocalValidatorFactoryBean.class).withPropertyValues("nrich.form-configuration.default-converter-enabled=false").run(context -> assertThat(context).doesNotHaveBean(ConstrainedPropertyValidatorConverterService.class));
    }
}
