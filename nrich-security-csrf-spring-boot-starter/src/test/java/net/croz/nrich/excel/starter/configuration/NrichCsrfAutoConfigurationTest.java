package net.croz.nrich.excel.starter.configuration;

import net.croz.nrich.security.csrf.configuration.NrichCsrfAutoConfiguration;
import net.croz.nrich.security.csrf.core.controller.CsrfPingController;
import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.webflux.filter.CsrfWebFilter;
import net.croz.nrich.security.csrf.webmvc.interceptor.CsrfInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class NrichCsrfAutoConfigurationTest {

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichCsrfAutoConfiguration.class));

    private final ReactiveWebApplicationContextRunner reactiveWebContextRunner = new ReactiveWebApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichCsrfAutoConfiguration.class));

    @Test
    void shouldConfigureWebMvcConfiguration() {
        webContextRunner.run(context -> {
            assertThat(context).hasSingleBean(CsrfTokenManagerService.class);
            assertThat(context).hasSingleBean(CsrfPingController.class);
            assertThat(context).hasSingleBean(CsrfInterceptor.class);
        });
    }

    @Test
    void shouldConfigureReactiveConfiguration() {
        reactiveWebContextRunner.run(context -> {
            assertThat(context).hasSingleBean(CsrfTokenManagerService.class);
            assertThat(context).hasSingleBean(CsrfPingController.class);
            assertThat(context).hasSingleBean(CsrfWebFilter.class);
        });
    }
}
