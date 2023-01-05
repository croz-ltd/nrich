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

package net.croz.nrich.security.csrf.configuration;

import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.controller.CsrfPingController;
import net.croz.nrich.security.csrf.core.service.AesCsrfTokenManagerService;
import net.croz.nrich.security.csrf.properties.NrichCsrfProperties;
import net.croz.nrich.security.csrf.webflux.filter.CsrfWebFilter;
import net.croz.nrich.security.csrf.webmvc.interceptor.CsrfInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ConditionalOnProperty(name = "nrich.security.csrf.active", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(NrichCsrfProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichCsrfAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public CsrfTokenManagerService tokenManagerService(NrichCsrfProperties csrfProperties) {
        return new AesCsrfTokenManagerService(csrfProperties.getTokenExpirationInterval(), csrfProperties.getTokenFutureThreshold(), csrfProperties.getCryptoKeyLength());
    }

    @ConditionalOnMissingBean
    @Bean
    public CsrfPingController csrfPingController() {
        return new CsrfPingController();
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean
    @Bean
    public CsrfInterceptor csrfInterceptor(CsrfTokenManagerService csrfTokenManagerService, NrichCsrfProperties csrfProperties) {
        return new CsrfInterceptor(
            csrfTokenManagerService, csrfProperties.getTokenKeyName(), csrfProperties.getInitialTokenUrl(), csrfProperties.getCsrfPingUri(), csrfProperties.getCsrfExcludeConfigList()
        );
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @ConditionalOnMissingBean
    @Bean
    public CsrfWebFilter webFilter(CsrfTokenManagerService csrfTokenManagerService, NrichCsrfProperties csrfProperties) {
        return new CsrfWebFilter(
            csrfTokenManagerService, csrfProperties.getTokenKeyName(), csrfProperties.getInitialTokenUrl(), csrfProperties.getCsrfPingUri(), csrfProperties.getCsrfExcludeConfigList()
        );
    }

    @ConditionalOnBean(CsrfInterceptor.class)
    @Bean
    public WebMvcConfigurer csrfInterceptorWebMvcConfigurer(CsrfInterceptor csrfInterceptor) {
        return new WebMvcConfigurer() {

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(csrfInterceptor);
            }

        };
    }
}
