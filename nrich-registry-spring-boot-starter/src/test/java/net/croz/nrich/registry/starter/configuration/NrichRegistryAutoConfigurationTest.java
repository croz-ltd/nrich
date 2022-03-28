package net.croz.nrich.registry.starter.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.formconfiguration.api.customizer.FormConfigurationMappingCustomizer;
import net.croz.nrich.registry.api.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.api.data.service.RegistryDataService;
import net.croz.nrich.registry.api.history.service.RegistryHistoryService;
import net.croz.nrich.registry.configuration.controller.RegistryConfigurationController;
import net.croz.nrich.registry.core.service.RegistryConfigurationResolverService;
import net.croz.nrich.registry.data.controller.RegistryDataController;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import net.croz.nrich.registry.history.controller.RegistryHistoryController;
import net.croz.nrich.registry.security.interceptor.RegistryConfigurationUpdateInterceptor;
import net.croz.nrich.registry.starter.configuration.stub.ModelMapperTestEntity;
import net.croz.nrich.registry.starter.configuration.stub.RegistryUserConfiguration;
import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;

class NrichRegistryAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(
        AutoConfigurations.of(DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, NrichRegistryAutoConfiguration.class)
    );

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner().withConfiguration(
        AutoConfigurations.of(DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, NrichRegistryAutoConfiguration.class)
    );

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
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
            assertThat(context).hasSingleBean(RegistryHistoryService.class);
            assertThat(context).hasSingleBean(FormConfigurationMappingCustomizer.class);

            assertThat(context).doesNotHaveBean(RegistryConfigurationController.class);
            assertThat(context).doesNotHaveBean(RegistryDataController.class);
            assertThat(context).doesNotHaveBean(RegistryHistoryController.class);
        });
    }

    @Test
    void shouldRegisterControllersInWebEnvironment() {
        // expect
        webContextRunner.withUserConfiguration(RegistryUserConfiguration.class).withBean(LocalValidatorFactoryBean.class).run(context -> {
            assertThat(context).hasSingleBean(RegistryConfigurationController.class);
            assertThat(context).hasSingleBean(RegistryDataController.class);
            assertThat(context).hasSingleBean(RegistryHistoryController.class);
        });
    }

    @Test
    void shouldNotCreateDefaultValueConverterWhenCreationIsDisabled() {
        contextRunner.withPropertyValues("nrich.registry.default-converter-enabled=false").run(context ->
            assertThat(context).doesNotHaveBean(StringToTypeConverter.class)
        );
    }

    @Test
    void shouldSkipIdCopyInRegisteredModelMapperInstance() {
        contextRunner.withUserConfiguration(RegistryUserConfiguration.class).withBean(LocalValidatorFactoryBean.class).run(context -> {
            // given
            ModelMapper modelMapper = context.getBean("registryDataModelMapper", ModelMapper.class);
            ModelMapperTestEntity modelMapperTestEntity = new ModelMapperTestEntity();

            modelMapperTestEntity.setName("name");
            modelMapperTestEntity.setId("id");

            // when
            ModelMapperTestEntity result = new ModelMapperTestEntity();
            modelMapper.map(modelMapperTestEntity, result);

            // then
            assertThat(result.getId()).isEqualTo("initial");
            assertThat(result.getName()).isEqualTo("name");
        });
    }
}
