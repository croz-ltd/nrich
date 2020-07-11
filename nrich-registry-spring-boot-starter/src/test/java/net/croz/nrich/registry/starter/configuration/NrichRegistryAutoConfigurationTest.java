package net.croz.nrich.registry.starter.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.registry.configuration.controller.RegistryConfigurationController;
import net.croz.nrich.registry.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.core.service.RegistryConfigurationResolverService;
import net.croz.nrich.registry.data.controller.RegistryDataController;
import net.croz.nrich.registry.data.service.RegistryDataFormConfigurationResolverService;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import net.croz.nrich.registry.data.service.RegistryDataService;
import net.croz.nrich.registry.security.interceptor.RegistryConfigurationUpdateInterceptor;
import net.croz.nrich.registry.starter.configuration.stub.RegistryUserConfiguration;
import net.croz.nrich.registry.starter.configuration.stub.RegistryUserFormConfiguration;
import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;

public class NrichRegistryAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, NrichRegistryAutoConfiguration.class));

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, NrichRegistryAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        contextRunner.withUserConfiguration(RegistryUserConfiguration.class).withBean(LocalValidatorFactoryBean.class).run(context -> {
            assertThat(context).hasBean("registryDataModelMapper");
            assertThat(context).hasBean("registryBaseModelMapper");

            assertThat(context).hasSingleBean(ObjectMapper.class);
            assertThat(context).hasSingleBean(StringToTypeConverter.class);
            assertThat(context).hasSingleBean(StringToEntityPropertyMapConverter.class);
            assertThat(context).hasSingleBean(RegistryConfigurationResolverService.class);
            assertThat(context).hasSingleBean(RegistryConfigurationUpdateInterceptor.class);
            assertThat(context).hasSingleBean(RegistryConfigurationService.class);
            assertThat(context).hasSingleBean(RegistryDataService.class);
            assertThat(context).hasSingleBean(RegistryDataRequestConversionService.class);

            assertThat(context).doesNotHaveBean(RegistryConfigurationController.class);
            assertThat(context).doesNotHaveBean(RegistryDataController.class);
            assertThat(context).doesNotHaveBean(RegistryDataFormConfigurationResolverService.class);
        });
    }

    @Test
    void shouldRegisterControllersInWebEnvironment() {
        webContextRunner.withUserConfiguration(RegistryUserConfiguration.class).withBean(LocalValidatorFactoryBean.class).run(context -> {
            assertThat(context).hasSingleBean(RegistryConfigurationController.class);
            assertThat(context).hasSingleBean(RegistryDataController.class);
        });
    }

    @Test
    void shouldRegisterFormConfigurationResolverServiceWhenFormConfigurationBeanIsDefined() {
        contextRunner.withUserConfiguration(RegistryUserFormConfiguration.class).withUserConfiguration(RegistryUserConfiguration.class).withBean(LocalValidatorFactoryBean.class).run(context -> {
            assertThat(context).hasSingleBean(RegistryDataFormConfigurationResolverService.class);
        });
    }

    @Test
    void shouldNotCreateDefaultValueConverterWhenCreationIsDisabled() {
        contextRunner.withPropertyValues("nrich.registry.default-converter-enabled=false").run(context -> assertThat(context).doesNotHaveBean(StringToTypeConverter.class));
    }
}
