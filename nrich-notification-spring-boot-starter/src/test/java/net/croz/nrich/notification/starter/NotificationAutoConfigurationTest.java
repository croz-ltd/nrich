package net.croz.nrich.notification.starter;

import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.api.service.NotificationResponseService;
import net.croz.nrich.notification.service.ConstraintConversionService;
import net.croz.nrich.notification.starter.configuration.NotificationAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NotificationAutoConfiguration.class));

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(NotificationAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ConstraintConversionService.class);
            assertThat(context).hasSingleBean(NotificationResolverService.class);
            assertThat(context).doesNotHaveBean(NotificationResponseService.class);
        });
    }

    @Test
    void shouldIncludeNotificationResponseServiceWhenRunningInWebEnvironment() {
        webContextRunner.run(context -> assertThat(context).hasSingleBean(NotificationResponseService.class));
    }
}