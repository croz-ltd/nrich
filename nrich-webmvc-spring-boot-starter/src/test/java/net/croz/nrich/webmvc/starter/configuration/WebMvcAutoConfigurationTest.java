package net.croz.nrich.webmvc.starter.configuration;

import net.croz.nrich.logging.api.service.LoggingService;
import net.croz.nrich.webmvc.advice.ControllerEditorRegistrationAdvice;
import net.croz.nrich.webmvc.advice.NotificationErrorHandlingRestControllerAdvice;
import net.croz.nrich.webmvc.api.service.ExceptionAuxiliaryDataResolverService;
import net.croz.nrich.webmvc.service.TransientPropertyResolverService;
import net.croz.nrich.webmvc.starter.configuration.stub.LoggingTestService;
import net.croz.nrich.webmvc.starter.configuration.stub.NotificationResponseTestService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.support.ResourceBundleMessageSource;

import static org.assertj.core.api.Assertions.assertThat;

public class WebMvcAutoConfigurationTest {

    private final WebApplicationContextRunner webApplicationContextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(WebMvcAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        webApplicationContextRunner.withBean(ResourceBundleMessageSource.class).withBean(LoggingTestService.class).withBean(NotificationResponseTestService.class).run(context -> {
            assertThat(context).hasSingleBean(LoggingService.class);
            assertThat(context).hasSingleBean(TransientPropertyResolverService.class);
            assertThat(context).hasSingleBean(ExceptionAuxiliaryDataResolverService.class);
            assertThat(context).hasSingleBean(ControllerEditorRegistrationAdvice.class);
            assertThat(context).hasSingleBean(NotificationErrorHandlingRestControllerAdvice.class);
        });
    }
}
