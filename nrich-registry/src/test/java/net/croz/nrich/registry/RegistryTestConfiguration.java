package net.croz.nrich.registry;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import net.croz.nrich.registry.api.model.RegistryCategoryDefinitionConfiguration;
import net.croz.nrich.registry.api.model.RegistryConfiguration;
import net.croz.nrich.registry.api.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.model.RegistryOverrideConfigurationHolder;
import net.croz.nrich.registry.configuration.controller.RegistryConfigurationController;
import net.croz.nrich.registry.configuration.service.DefaultRegistryConfigurationService;
import net.croz.nrich.registry.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.configuration.stub.RegistryConfigurationTestEntity;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.service.DefaultRegistryConfigurationResolverService;
import net.croz.nrich.registry.core.service.RegistryConfigurationResolverService;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.controller.RegistryDataController;
import net.croz.nrich.registry.data.interceptor.RegistryDataInterceptor;
import net.croz.nrich.registry.data.service.DefaultRegistryDataFormConfigurationResolverService;
import net.croz.nrich.registry.data.service.DefaultRegistryDataRequestConversionService;
import net.croz.nrich.registry.data.service.DefaultRegistryDataService;
import net.croz.nrich.registry.core.service.EntityManagerRegistryEntityFinderService;
import net.croz.nrich.registry.data.service.RegistryDataFormConfigurationResolverService;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import net.croz.nrich.registry.data.service.RegistryDataService;
import net.croz.nrich.registry.core.service.RegistryEntityFinderService;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenFormConfiguration;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenSearchConfiguration;
import net.croz.nrich.registry.history.controller.RegistryHistoryController;
import net.croz.nrich.registry.history.service.DefaultRegistryHistoryService;
import net.croz.nrich.registry.history.service.RegistryHistoryService;
import net.croz.nrich.registry.security.interceptor.RegistryConfigurationUpdateInterceptor;
import net.croz.nrich.registry.security.stub.RegistryConfigurationUpdateInterceptorTestEntity;
import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import net.croz.nrich.search.api.model.SearchConfiguration;
import net.croz.nrich.search.api.model.operator.DefaultSearchOperator;
import net.croz.nrich.search.api.model.operator.SearchOperatorOverride;
import net.croz.nrich.search.converter.DefaultStringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.DefaultStringToTypeConverter;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ModelMapper registryDataModelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        final Condition<Object, Object> skipIds = context -> !context.getMapping().getLastDestinationProperty().getName().equals("id");

        modelMapper.getConfiguration().setPropertyCondition(skipIds);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }

    @Bean
    public ModelMapper registryHistoryModelMapper() {
        final ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);

        objectMapper.setDateFormat(new StdDateFormat());

        return objectMapper;
    }

    @Bean
    public MessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename("messages");

        return messageSource;
    }

    @Bean
    public RegistryWebMvcTestConfiguration registryWebMvcConfiguration(final ObjectMapper objectMapper) {
        return new RegistryWebMvcTestConfiguration(objectMapper);
    }

    @Bean
    public StringToTypeConverter<?> defaultStringToTypeConverter() {
        return new DefaultStringToTypeConverter(Arrays.asList("dd.MM.yyyy", "yyyy-MM-dd'T'HH:mm"), Arrays.asList("#0.00", "#0,00"), "^(?i)\\s*(true|yes)\\s*$", "^(?i)\\s*(false|no)\\s*$");
    }

    @Bean
    public StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter(final List<StringToTypeConverter<?>> stringToTypeConverterList) {
        return new DefaultStringToEntityPropertyMapConverter(stringToTypeConverterList);
    }

    @Bean
    public RegistryConfiguration registryConfiguration() {
        final RegistryConfiguration registryConfiguration = new RegistryConfiguration();

        final RegistryCategoryDefinitionConfiguration registryDataConfigurationCategory = new RegistryCategoryDefinitionConfiguration();

        registryDataConfigurationCategory.setRegistryCategoryId("DATA");
        registryDataConfigurationCategory.setIncludeEntityPatternList(Collections.singletonList("^net.croz.nrich.registry.data.stub.*$"));
        registryDataConfigurationCategory.setExcludeEntityPatternList(Collections.singletonList("^net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroupId"));

        final RegistryCategoryDefinitionConfiguration registryConfigurationConfigurationCategory = new RegistryCategoryDefinitionConfiguration();

        registryConfigurationConfigurationCategory.setRegistryCategoryId("CONFIGURATION");
        registryConfigurationConfigurationCategory.setIncludeEntityPatternList(Collections.singletonList("^net.croz.nrich.registry.configuration.stub.*$"));

        final RegistryCategoryDefinitionConfiguration registryHistoryConfigurationCategory = new RegistryCategoryDefinitionConfiguration();

        registryHistoryConfigurationCategory.setRegistryCategoryId("HISTORY");
        registryHistoryConfigurationCategory.setIncludeEntityPatternList(Collections.singletonList("^net.croz.nrich.registry.history.stub.*$"));

        registryConfiguration.setRegistryCategoryDisplayOrderList(Arrays.asList("CONFIGURATION", "DATA", "HISTORY"));
        registryConfiguration.setRegistryCategoryDefinitionConfigurationList(Arrays.asList(registryDataConfigurationCategory, registryConfigurationConfigurationCategory, registryHistoryConfigurationCategory));

        final RegistryOverrideConfiguration registryOverrideConfiguration = RegistryOverrideConfiguration.defaultConfiguration();

        registryOverrideConfiguration.setPropertyDisplayOrderList(Arrays.asList("name", "id", "nonEditableProperty", "floatNumber", "doubleNumber"));
        registryOverrideConfiguration.setIgnoredPropertyList(Collections.singletonList("skippedProperty"));
        registryOverrideConfiguration.setNonEditablePropertyList(Collections.singletonList("nonEditableProperty"));
        registryOverrideConfiguration.setNonSortablePropertyList(Collections.singletonList("nonEditableProperty"));
        registryOverrideConfiguration.setDeletable(false);

        final RegistryOverrideConfiguration registryInterceptorTestOverrideConfiguration = RegistryOverrideConfiguration.defaultConfiguration();

        registryInterceptorTestOverrideConfiguration.setReadOnly(true);

        final RegistryOverrideConfigurationHolder registryTestEntityOverrideConfiguration = RegistryOverrideConfigurationHolder.builder()
                .type(RegistryConfigurationTestEntity.class).registryOverrideConfiguration(registryOverrideConfiguration).build();

        final RegistryOverrideConfigurationHolder registryInterceptorTestEntityOverrideConfiguration = RegistryOverrideConfigurationHolder.builder()
                .type(RegistryConfigurationUpdateInterceptorTestEntity.class).registryOverrideConfiguration(registryInterceptorTestOverrideConfiguration).build();

        final SearchConfiguration<Object, Object, Map<String, Object>> searchConfiguration = SearchConfiguration.emptyConfiguration();
        searchConfiguration.setSearchOperatorOverrideList(Collections.singletonList(SearchOperatorOverride.forType(String.class, DefaultSearchOperator.EQ)));

        final RegistryOverrideConfigurationHolder registryOverrideConfigurationHolder = RegistryOverrideConfigurationHolder.builder()
                .type(RegistryTestEntityWithOverriddenSearchConfiguration.class).registryDataOverrideSearchConfiguration(searchConfiguration).build();

        registryConfiguration.setRegistryOverrideConfigurationHolderList(Arrays.asList(registryOverrideConfigurationHolder, registryTestEntityOverrideConfiguration, registryInterceptorTestEntityOverrideConfiguration));

        return registryConfiguration;
    }

    @Bean
    public RegistryConfigurationResolverService registryConfigurationResolverService(final EntityManager entityManager, final RegistryConfiguration registryConfiguration) {
        return new DefaultRegistryConfigurationResolverService(entityManager, registryConfiguration);
    }

    @Bean
    public RegistryConfigurationUpdateInterceptor registryConfigurationUpdateInterceptor(final  RegistryConfigurationResolverService registryConfigurationResolverService) {
        return new RegistryConfigurationUpdateInterceptor(registryConfigurationResolverService.resolveRegistryOverrideConfigurationMap());
    }

    @Bean
    public RegistryConfigurationService registryConfigurationService(final MessageSource messageSource, final RegistryConfigurationResolverService registryConfigurationResolverService) {
        final List<String> defaultReadOnlyPropertyList = Arrays.asList("id", "version");

        return new DefaultRegistryConfigurationService(messageSource, defaultReadOnlyPropertyList, registryConfigurationResolverService.resolveRegistryGroupDefinition(), registryConfigurationResolverService.resolveRegistryHistoryConfiguration(), registryConfigurationResolverService.resolveRegistryOverrideConfigurationMap());
    }

    @Bean
    public RegistryConfigurationController registryConfigurationController(final RegistryConfigurationService registryConfigurationService) {
        return new RegistryConfigurationController(registryConfigurationService);
    }

    @Bean
    public RegistryEntityFinderService registryEntityFinderService(final EntityManager entityManager) {
        return new EntityManagerRegistryEntityFinderService(entityManager);
    }

    @Bean
    public RegistryDataService registryDataService(final EntityManager entityManager, final ModelMapper registryDataModelMapper, final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter, final RegistryConfigurationResolverService registryConfigurationResolverService, @Autowired(required = false) final List<RegistryDataInterceptor> interceptorList, final RegistryEntityFinderService registryEntityFinderService) {
        return new DefaultRegistryDataService(entityManager, registryDataModelMapper, stringToEntityPropertyMapConverter, registryConfigurationResolverService.resolveRegistryDataConfiguration(), Optional.ofNullable(interceptorList).orElse(Collections.emptyList()), registryEntityFinderService);
    }

    @Bean
    public RegistryDataRequestConversionService registryDataRequestConversionService(final ObjectMapper objectMapper, final RegistryConfigurationResolverService registryConfigurationResolverService) {
        return new DefaultRegistryDataRequestConversionService(objectMapper, registryConfigurationResolverService.resolveRegistryDataConfiguration());
    }

    @Bean
    public RegistryDataController registryDataController(final RegistryDataService registryDataService, final RegistryDataRequestConversionService registryDataRequestConversionService, final Validator validator) {
        return new RegistryDataController(registryDataService, registryDataRequestConversionService, validator);
    }

    @Bean
    public RegistryHistoryService registryHistoryService(final EntityManager entityManager, final RegistryConfigurationResolverService registryConfigurationResolverService, final ModelMapper registryHistoryModelMapper) {
        return new DefaultRegistryHistoryService(entityManager, registryConfigurationResolverService.resolveRegistryDataConfiguration(), registryConfigurationResolverService.resolveRegistryHistoryConfiguration(), registryHistoryModelMapper);
    }

    @Bean
    public RegistryHistoryController registryHistoryController(final RegistryHistoryService registryHistoryService) {
        return new RegistryHistoryController(registryHistoryService);
    }

    @Bean
    public RegistryDataFormConfigurationResolverService registryFormConfigurationRegistrationService(final RegistryConfigurationResolverService registryConfigurationResolverService) {
        final List<Class<?>> registryClassList = registryConfigurationResolverService.resolveRegistryDataConfiguration().getRegistryDataConfigurationList().stream()
                .map(RegistryDataConfiguration::getRegistryType)
                .collect(Collectors.toList());

        final Map<String, Class<?>> formConfigurationMap = new HashMap<>();

        formConfigurationMap.put(String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, RegistryTestEntityWithOverriddenFormConfiguration.class.getName(), RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX), Object.class);
        formConfigurationMap.put(String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, RegistryTestEntityWithOverriddenFormConfiguration.class.getName(), RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX), Object.class);

        return new DefaultRegistryDataFormConfigurationResolverService(registryClassList, formConfigurationMap);
    }
}
