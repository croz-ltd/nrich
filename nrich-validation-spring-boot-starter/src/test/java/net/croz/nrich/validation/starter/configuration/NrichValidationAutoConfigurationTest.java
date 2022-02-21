package net.croz.nrich.validation.starter.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class NrichValidationAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichValidationAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(NrichValidationAutoConfiguration.ValidationMessageSourceRegistrar.class);
        });
    }

    @Test
    void shouldNotRegisterValidationMessagesWhenDisabledViaProperty() {
        contextRunner.withPropertyValues("nrich.validation.register-messages=false").run(context -> {
            assertThat(context).doesNotHaveBean(NrichValidationAutoConfiguration.ValidationMessageSourceRegistrar.class);
        });
    }
}
