package net.croz.nrich.notification.starter;

import net.croz.nrich.notification.api.service.BaseNotificationResponseService;
import net.croz.nrich.notification.api.service.ConstraintConversionService;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.starter.configuration.NrichNotificationAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractResourceBasedMessageSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NrichNotificationAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichNotificationAutoConfiguration.class));

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichNotificationAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ConstraintConversionService.class);
            assertThat(context).hasSingleBean(NotificationResolverService.class);
            assertThat(context).doesNotHaveBean(BaseNotificationResponseService.class);
            assertThat(context).hasSingleBean(NrichNotificationAutoConfiguration.NotificationMessageSourceRegistrar.class);
        });
    }

    @Test
    void shouldNotRegisterMessagesWhenExplicitlyDisalbed() {
        // expect
        contextRunner.withPropertyValues("nrich.notification.register-messages=false").run(context ->
            assertThat(context).doesNotHaveBean(NrichNotificationAutoConfiguration.NotificationMessageSourceRegistrar.class)
        );
    }

    @Test
    void shouldIncludeNotificationResponseServiceWhenRunningInWebEnvironment() {
        // expect
        webContextRunner.run(context ->
            assertThat(context).hasSingleBean(BaseNotificationResponseService.class)
        );
    }

    @Test
    void shouldRegisterMessagesWhenPossible() {
        // given
        AbstractResourceBasedMessageSource messageSource = mock(AbstractResourceBasedMessageSource.class);

        // expect
        contextRunner.withBean(MessageSource.class, () -> messageSource).run(context ->
            verify(messageSource).addBasenames(NrichNotificationAutoConfiguration.NOTIFICATION_MESSAGES_NAME)
        );
    }
}
