package net.croz.nrich.security.csrf.configuration;

import net.croz.nrich.security.csrf.core.controller.CsrfPingController;
import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.service.AesCsrfTokenManagerService;
import net.croz.nrich.security.csrf.properties.NrichCsrfProperties;
import net.croz.nrich.security.csrf.webflux.filter.CsrfWebFilter;
import net.croz.nrich.security.csrf.webmvc.interceptor.CsrfInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(NrichCsrfProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichCsrfAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public CsrfTokenManagerService tokenManagerService(final NrichCsrfProperties csrfProperties) {
        return new AesCsrfTokenManagerService(csrfProperties.getTokenExpirationInterval(), csrfProperties.getTokenFutureThreshold(), csrfProperties.getTokenKeyName(), csrfProperties.getCryptoKeyLength());
    }

    @Bean
    public CsrfPingController csrfPingController() {
        return new CsrfPingController();
    }

    @ConditionalOnProperty(name = "nrich.security.csrf.active", havingValue = "true", matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @Bean
    public CsrfInterceptor csrfInterceptor(final CsrfTokenManagerService csrfTokenManagerService, final NrichCsrfProperties csrfProperties) {
        return new CsrfInterceptor(csrfTokenManagerService, csrfProperties.getInitialTokenUrl(), csrfProperties.getCsrfPingUrl(), csrfProperties.getCsrfExcludeConfigList());
    }

    @ConditionalOnProperty(name = "nrich.security.csrf.active", havingValue = "true", matchIfMissing = true)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @Bean
    public CsrfWebFilter webFilter(final CsrfTokenManagerService csrfTokenManagerService, final NrichCsrfProperties csrfProperties) {
        return new CsrfWebFilter(csrfTokenManagerService, csrfProperties.getInitialTokenUrl(), csrfProperties.getCsrfPingUrl(), csrfProperties.getCsrfExcludeConfigList());
    }
}
