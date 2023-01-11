/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.excel.starter.configuration;

import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.configuration.NrichCsrfAutoConfiguration;
import net.croz.nrich.security.csrf.core.controller.CsrfPingController;
import net.croz.nrich.security.csrf.webflux.filter.CsrfWebFilter;
import net.croz.nrich.security.csrf.webmvc.interceptor.CsrfInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class NrichCsrfAutoConfigurationTest {

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichCsrfAutoConfiguration.class));

    private final ReactiveWebApplicationContextRunner reactiveWebContextRunner = new ReactiveWebApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichCsrfAutoConfiguration.class));

    @Test
    void shouldConfigureWebMvcConfiguration() {
        // expect
        webContextRunner.run(context -> {
            assertThat(context).hasSingleBean(CsrfTokenManagerService.class);
            assertThat(context).hasSingleBean(CsrfPingController.class);
            assertThat(context).hasSingleBean(CsrfInterceptor.class);
        });
    }

    @Test
    void shouldConfigureReactiveConfiguration() {
        // expect
        reactiveWebContextRunner.run(context -> {
            assertThat(context).hasSingleBean(CsrfTokenManagerService.class);
            assertThat(context).hasSingleBean(CsrfPingController.class);
            assertThat(context).hasSingleBean(CsrfWebFilter.class);
        });
    }

    @Test
    void shouldNotIncludeCsrfConfigurationWhenItIsDisabled() {
        // expect
        webContextRunner.withPropertyValues("nrich.security.csrf.active=false").run(context -> {
            assertThat(context).doesNotHaveBean(CsrfTokenManagerService.class);
            assertThat(context).doesNotHaveBean(CsrfPingController.class);
            assertThat(context).doesNotHaveBean(CsrfInterceptor.class);
        });
    }
}
