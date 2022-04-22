package net.croz.nrich.logging.starter.configuration;

import net.croz.nrich.logging.api.service.LoggingService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.support.ResourceBundleMessageSource;

import static org.assertj.core.api.Assertions.assertThat;

class NrichLoggingAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichLoggingAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.withBean(ResourceBundleMessageSource.class).run(context ->
            assertThat(context).hasSingleBean(LoggingService.class)
        );
    }

    @Test
    void shouldNotConfigureWhenMessageSourceIsMissing() {
        // expect
        contextRunner.run(context ->
            assertThat(context).doesNotHaveBean(LoggingService.class)
        );
    }
}
