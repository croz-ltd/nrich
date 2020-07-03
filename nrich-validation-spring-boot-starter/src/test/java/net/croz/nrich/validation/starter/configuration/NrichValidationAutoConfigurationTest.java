package net.croz.nrich.validation.starter.configuration;

import net.croz.nrich.validation.constraint.validator.ValidFileValidatorProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class NrichValidationAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichValidationAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ValidFileValidatorProperties.class);
            assertThat(context).getBean(ValidFileValidatorProperties.class).isInstanceOf(ValidFileValidatorProperties.class);
            assertThat(context).hasSingleBean(NrichValidationAutoConfiguration.ValidationMessageSourceRegistrar.class);
        });
    }

    @Test
    void shouldLoadFileValidationPropertiesValues() {
        contextRunner.withPropertyValues(
                "nrich.validation.file-validation.validation-enabled=true",
                "nrich.validation.file-validation.allowed-extension-list=txt,pdf",
                "nrich.validation.file-validation.allowed-content-type-list=text/plain,application/pdf",
                "nrich.validation.file-validation.allowed-file-name-regex=(?U)[\\w-.]+"
        ).run(context -> {
            // when
            final ValidFileValidatorProperties fileValidationProperties = context.getBean(ValidFileValidatorProperties.class);

            // then
            assertThat(fileValidationProperties.getValidationEnabled()).isEqualTo(true);
            assertThat(fileValidationProperties.getAllowedExtensionList()).isEqualTo(Arrays.asList("txt", "pdf"));
            assertThat(fileValidationProperties.getAllowedContentTypeList()).isEqualTo(Arrays.asList("text/plain", "application/pdf"));
            assertThat(fileValidationProperties.getAllowedFileNameRegex()).isEqualTo("(?U)[\\w-.]+");
        });
    }

    @Test
    void shouldNotRegisterValidationMessagesWhenDisabledViaProperty() {
        contextRunner.withPropertyValues("nrich.validation.register-messages=false").run(context -> {
           assertThat(context).doesNotHaveBean(NrichValidationAutoConfiguration.ValidationMessageSourceRegistrar.class);
        });
    }
}
