package net.croz.nrich.excel.starter.configuration;

import net.croz.nrich.excel.starter.configuration.stub.FormConfigurationTestRequest;
import net.croz.nrich.formconfiguration.api.service.ConstrainedPropertyValidatorConverterService;
import net.croz.nrich.formconfiguration.api.service.FormConfigurationService;
import net.croz.nrich.formconfiguration.controller.FormConfigurationController;
import net.croz.nrich.formconfiguration.service.FieldErrorMessageResolverService;
import net.croz.nrich.formconfiguration.starter.configuration.NrichFormConfigurationAutoConfiguration;
import net.croz.nrich.formconfiguration.starter.properties.NrichFormConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NrichFormConfigurationAutoConfigurationTest {

    private static final String ENTRY_FORMAT = "nrich.form-configuration.form-configuration-mapping.%s=%s";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichFormConfigurationAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.withBean(LocalValidatorFactoryBean.class).run(context -> {
            assertThat(context).hasSingleBean(FieldErrorMessageResolverService.class);
            assertThat(context).hasSingleBean(FormConfigurationService.class);
            assertThat(context).hasSingleBean(FormConfigurationController.class);
        });
    }

    @Test
    void shouldNotConfigureWhenNoValidatorBeanIsPresent() {
        // expect
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(FieldErrorMessageResolverService.class);
            assertThat(context).doesNotHaveBean(FormConfigurationService.class);
            assertThat(context).doesNotHaveBean(FormConfigurationController.class);
        });
    }

    @Test
    void shouldNotCreateDefaultValueConverterWhenCreationIsDisabled() {
        // expect
        contextRunner.withBean(LocalValidatorFactoryBean.class).withPropertyValues("nrich.form-configuration.default-converter-enabled=false").run(context ->
            assertThat(context).doesNotHaveBean(ConstrainedPropertyValidatorConverterService.class)
        );
    }

    @Test
    void shouldRegisterFormConfigurationMapping() {
        // given
        String createKey = "create-form";
        String updateKey = "update-form";
        Class<?> requestType = FormConfigurationTestRequest.class;
        String[] propertyValues = new String[] { String.format(ENTRY_FORMAT, createKey, requestType.getName()), String.format(ENTRY_FORMAT, updateKey, requestType.getName()) };

        contextRunner.withBean(LocalValidatorFactoryBean.class).withPropertyValues(propertyValues).run(context -> {
            // when
            Map<String, Class<?>> formConfigurationMapping = context.getBean(NrichFormConfigurationProperties.class).getFormConfigurationMapping();

            // then
            assertThat(formConfigurationMapping).containsEntry(createKey, requestType).containsEntry(updateKey, requestType);
        });
    }
}
