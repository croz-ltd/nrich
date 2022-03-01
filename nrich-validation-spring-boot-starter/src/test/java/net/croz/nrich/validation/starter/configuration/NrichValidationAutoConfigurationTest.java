package net.croz.nrich.validation.starter.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractResourceBasedMessageSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NrichValidationAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichValidationAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.run(context -> assertThat(context).hasSingleBean(NrichValidationAutoConfiguration.ValidationMessageSourceRegistrar.class));
    }

    @Test
    void shouldNotRegisterValidationMessagesWhenDisabledViaProperty() {
        // expect
        contextRunner.withPropertyValues("nrich.validation.register-messages=false")
                .run(context -> assertThat(context).doesNotHaveBean(NrichValidationAutoConfiguration.ValidationMessageSourceRegistrar.class));
    }

    @Test
    void shouldRegisterMessagesWhenPossible() {
        // given
        AbstractResourceBasedMessageSource messageSource = mock(AbstractResourceBasedMessageSource.class);

        // expect
        contextRunner.withBean(MessageSource.class, () -> messageSource).run(context ->
                verify(messageSource).addBasenames(NrichValidationAutoConfiguration.VALIDATION_MESSAGES_NAME)
        );
    }
}
