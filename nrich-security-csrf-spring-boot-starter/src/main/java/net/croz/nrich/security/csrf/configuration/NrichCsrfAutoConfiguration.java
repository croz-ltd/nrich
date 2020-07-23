package net.croz.nrich.security.csrf.configuration;

import net.croz.nrich.security.csrf.api.service.CsrfTokenManagerService;
import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
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
    public CsrfTokenManagerService tokenManagerService(final NrichCsrfProperties csrfProperties) {
        return new AesCsrfTokenManagerService(csrfProperties.getTokenExpirationInterval(), csrfProperties.getTokenFutureThreshold(), csrfProperties.getCryptoKeyLength());
    }

    @ConditionalOnProperty(name = "nrich.security.csrf.csrf-ping-url", havingValue = CsrfConstants.CSRF_DEFAULT_PING_URI, matchIfMissing = true)
    @Bean
    public CsrfPingController csrfPingController() {
        return new CsrfPingController();
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @Bean
    public CsrfInterceptor csrfInterceptor(final CsrfTokenManagerService csrfTokenManagerService, final NrichCsrfProperties csrfProperties) {
        return new CsrfInterceptor(csrfTokenManagerService, csrfProperties.getTokenKeyName(), csrfProperties.getInitialTokenUrl(), csrfProperties.getCsrfPingUri(), csrfProperties.getCsrfExcludeConfigList());
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @Bean
    public CsrfWebFilter webFilter(final CsrfTokenManagerService csrfTokenManagerService, final NrichCsrfProperties csrfProperties) {
        return new CsrfWebFilter(csrfTokenManagerService, csrfProperties.getTokenKeyName(), csrfProperties.getInitialTokenUrl(), csrfProperties.getCsrfPingUri(), csrfProperties.getCsrfExcludeConfigList());
    }

    @ConditionalOnBean(CsrfInterceptor.class)
    @Bean
    public WebMvcConfigurer csrfInterceptorWebMvcConfigurer(final CsrfInterceptor csrfInterceptor) {
        return new WebMvcConfigurer() {

            @Override
            public void addInterceptors(final InterceptorRegistry registry) {
                registry.addInterceptor(csrfInterceptor);
            }

        };
    }
}
