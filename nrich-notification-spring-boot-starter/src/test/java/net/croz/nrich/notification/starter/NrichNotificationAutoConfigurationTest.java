package net.croz.nrich.notification.starter;

import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.api.service.NotificationResponseService;
import net.croz.nrich.notification.service.ConstraintConversionService;
import net.croz.nrich.notification.starter.configuration.NrichNotificationAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class NrichNotificationAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichNotificationAutoConfiguration.class));

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichNotificationAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ConstraintConversionService.class);
            assertThat(context).hasSingleBean(NotificationResolverService.class);
            assertThat(context).doesNotHaveBean(NotificationResponseService.class);
            assertThat(context).hasSingleBean(NrichNotificationAutoConfiguration.NotificationMessageSourceRegistrar.class);
        });
    }

    @Test
    void shouldNotRegisterMessagesWhenExplicitlyDisalbed() {
        contextRunner.withPropertyValues("nrich.notification.register-messages=false").run(context -> {
            assertThat(context).doesNotHaveBean(NrichNotificationAutoConfiguration.NotificationMessageSourceRegistrar.class);
        });
    }

    @Test
    void shouldIncludeNotificationResponseServiceWhenRunningInWebEnvironment() {
        webContextRunner.run(context -> assertThat(context).hasSingleBean(NotificationResponseService.class));
    }
}
