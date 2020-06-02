package net.croz.nrich.registry;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.registry.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.configuration.service.impl.RegistryConfigurationServiceImpl;
import net.croz.nrich.registry.core.model.RegistryConfiguration;
import net.croz.nrich.registry.core.model.RegistryGroupDefinitionConfiguration;
import net.croz.nrich.registry.core.service.RegistryConfigurationResolverService;
import net.croz.nrich.registry.core.service.impl.RegistryConfigurationResolverServiceImpl;
import net.croz.nrich.registry.data.controller.RegistryDataController;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import net.croz.nrich.registry.data.service.RegistryDataService;
import net.croz.nrich.registry.data.service.impl.RegistryDataRequestConversionServiceImpl;
import net.croz.nrich.registry.data.service.impl.RegistryDataServiceImpl;
import net.croz.nrich.search.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.StringToTypeConverter;
import net.croz.nrich.search.converter.impl.DefaultStringToTypeConverter;
import net.croz.nrich.search.converter.impl.StringToEntityPropertyMapConverterImpl;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@EnableTransactionManagement
@EnableWebMvc
@EnableJpaRepositories
@Configuration(proxyBeanMethods = false)
public class RegistryTestConfiguration {

    @Bean(destroyMethod = "shutdown")
    public EmbeddedDatabase dataSource() {
        return new EmbeddedDatabaseBuilder().generateUniqueName(true).setType(EmbeddedDatabaseType.H2).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);

        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("net.croz.nrich.registry");
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);

        return entityManagerFactoryBean;
    }

    @Bean
    public JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(entityManagerFactory);

        return transactionManager;
    }

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        final Condition<Object, Object> skipIds = context -> !context.getMapping().getLastDestinationProperty().getName().equals("id");

        modelMapper.getConfiguration().setPropertyCondition(skipIds);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return objectMapper;
    }

    @Bean
    public StringToTypeConverter<?> defaultStringToTypeConverter() {
        return new DefaultStringToTypeConverter(Arrays.asList("dd.MM.yyyy", "yyyy-MM-dd'T'HH:mm"), Arrays.asList("#0.00", "#0,00"));
    }

    @Bean
    public StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter(final List<StringToTypeConverter<?>> stringToTypeConverterList) {
        return new StringToEntityPropertyMapConverterImpl(stringToTypeConverterList);
    }

    @Bean
    public RegistryConfiguration registryConfiguration() {
        final RegistryConfiguration registryConfiguration = new RegistryConfiguration();

        final RegistryGroupDefinitionConfiguration registryGroupDefinitionConfiguration = new RegistryGroupDefinitionConfiguration();

        registryGroupDefinitionConfiguration.setRegistryGroupId("DEFAULT");
        registryGroupDefinitionConfiguration.setIncludeEntityPatternList(Collections.singletonList("^net.croz.nrich.registry.data.stub.*$"));
        registryGroupDefinitionConfiguration.setExcludeEntityPatternList(Collections.singletonList("^net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroupId"));

        registryConfiguration.setRegistryGroupDefinitionConfigurationList(Collections.singletonList(registryGroupDefinitionConfiguration));

        return registryConfiguration;
    }

    @Bean
    public RegistryConfigurationResolverService registryConfigurationResolverService(final EntityManager entityManager, final RegistryConfiguration registryConfiguration) {
        return new RegistryConfigurationResolverServiceImpl(entityManager, registryConfiguration);
    }

    @Bean
    public RegistryConfigurationService registryConfigurationService(final MessageSource messageSource, final RegistryConfigurationResolverService registryConfigurationResolverService) {
        final List<String> defaultReadOnlyPropertyList = Arrays.asList("id", "version");
        return new RegistryConfigurationServiceImpl(messageSource, defaultReadOnlyPropertyList, registryConfigurationResolverService.resolveRegistryGroupDefinition(), registryConfigurationResolverService.resolveRegistryOverrideConfigurationMap());
    }

    @Bean
    public RegistryDataService registryDataService(final EntityManager entityManager, final ModelMapper modelMapper, final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter, final RegistryConfigurationResolverService registryConfigurationResolverService) {
        return new RegistryDataServiceImpl(entityManager, modelMapper, stringToEntityPropertyMapConverter, registryConfigurationResolverService.resolveRegistryDataConfiguration());
    }

    @Bean
    public RegistryDataRequestConversionService registryDataRequestConversionService(final ObjectMapper objectMapper, final RegistryConfigurationResolverService registryConfigurationResolverService) {
        return new RegistryDataRequestConversionServiceImpl(objectMapper, registryConfigurationResolverService.resolveRegistryDataConfiguration());
    }

    @Bean
    public RegistryDataController registryDataController(final RegistryDataService registryDataService, final RegistryDataRequestConversionService registryDataRequestConversionService, final Validator validator) {
        return new RegistryDataController(registryDataService, registryDataRequestConversionService, validator);
    }
}
