package net.croz.nrich.registry;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.croz.nrich.registry.data.controller.RegistryDataController;
import net.croz.nrich.registry.data.model.RegistrySearchConfiguration;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import net.croz.nrich.registry.data.service.RegistryDataService;
import net.croz.nrich.registry.data.service.impl.RegistryDataRequestConversionServiceImpl;
import net.croz.nrich.registry.data.service.impl.RegistryDataServiceImpl;
import net.croz.nrich.registry.data.stub.RegistryTestEntity;
import net.croz.nrich.search.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.StringToTypeConverter;
import net.croz.nrich.search.converter.impl.DefaultStringToTypeConverter;
import net.croz.nrich.search.converter.impl.StringToEntityPropertyMapConverterImpl;
import net.croz.nrich.search.model.SearchConfiguration;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
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
import org.springframework.validation.beanvalidation.MethodValidationInterceptor;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
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
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
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
    public RegistryDataService registryDataService(final EntityManager entityManager, final ModelMapper modelMapper, final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter) {
        final RegistrySearchConfiguration<?, ?> registrySearchConfiguration = new RegistrySearchConfiguration<>(RegistryTestEntity.class, SearchConfiguration.emptyConfigurationMatchingAny());

        return new RegistryDataServiceImpl(entityManager, modelMapper, stringToEntityPropertyMapConverter, Collections.singletonList(registrySearchConfiguration));
    }

    @Bean
    public RegistryDataRequestConversionService registryDataRequestConversionService(final ObjectMapper objectMapper) {
        final RegistrySearchConfiguration<?, ?> registrySearchConfiguration = new RegistrySearchConfiguration<>(RegistryTestEntity.class, SearchConfiguration.emptyConfigurationMatchingAny());

        return new RegistryDataRequestConversionServiceImpl(objectMapper, Collections.singletonList(registrySearchConfiguration));
    }

    @Bean
    public RegistryDataController registryDataController(final RegistryDataService registryDataService, final RegistryDataRequestConversionService registryDataRequestConversionService) {
        return new RegistryDataController(registryDataService, registryDataRequestConversionService);
    }
}
