package net.croz.nrich.registry;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import net.croz.nrich.registry.api.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.api.core.model.RegistryConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryGroupDefinitionConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfigurationHolder;
import net.croz.nrich.registry.api.core.service.RegistryEntityFinderService;
import net.croz.nrich.registry.api.data.interceptor.RegistryDataInterceptor;
import net.croz.nrich.registry.api.data.service.RegistryDataFormConfigurationResolverService;
import net.croz.nrich.registry.api.data.service.RegistryDataService;
import net.croz.nrich.registry.api.history.service.RegistryHistoryService;
import net.croz.nrich.registry.configuration.controller.RegistryConfigurationController;
import net.croz.nrich.registry.configuration.service.DefaultRegistryConfigurationService;
import net.croz.nrich.registry.configuration.stub.RegistryConfigurationTestEntity;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryGroupDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;
import net.croz.nrich.registry.core.service.DefaultRegistryConfigurationResolverService;
import net.croz.nrich.registry.core.service.EntityManagerRegistryEntityFinderService;
import net.croz.nrich.registry.core.service.RegistryConfigurationResolverService;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import net.croz.nrich.registry.data.constant.RegistryDataConstants;
import net.croz.nrich.registry.data.controller.RegistryDataController;
import net.croz.nrich.registry.data.service.DefaultRegistryDataFormConfigurationResolverService;
import net.croz.nrich.registry.data.service.DefaultRegistryDataRequestConversionService;
import net.croz.nrich.registry.data.service.DefaultRegistryDataService;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenFormConfiguration;
import net.croz.nrich.registry.data.stub.RegistryTestEntityWithOverriddenSearchConfiguration;
import net.croz.nrich.registry.history.controller.RegistryHistoryController;
import net.croz.nrich.registry.history.service.DefaultRegistryHistoryService;
import net.croz.nrich.registry.security.interceptor.RegistryConfigurationUpdateInterceptor;
import net.croz.nrich.registry.security.stub.RegistryConfigurationUpdateInterceptorNonModifiableEntity;
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
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("net.croz.nrich.registry");
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);

        return entityManagerFactoryBean;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(entityManagerFactory);

        return transactionManager;
    }

    @Bean
    public ModelMapper registryDataModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        Condition<Object, Object> skipIds = context -> !context.getMapping().getLastDestinationProperty().getName().equals("id");

        modelMapper.getConfiguration().setPropertyCondition(skipIds);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }

    @Bean
    public ModelMapper registryBaseModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

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
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

        messageSource.setBasename("messages");

        return messageSource;
    }

    @Bean
    public RegistryWebMvcTestConfiguration registryWebMvcConfiguration(ObjectMapper objectMapper) {
        return new RegistryWebMvcTestConfiguration(objectMapper);
    }

    @Bean
    public StringToTypeConverter<?> defaultStringToTypeConverter() {
        return new DefaultStringToTypeConverter(Arrays.asList("dd.MM.yyyy", "yyyy-MM-dd'T'HH:mm"), Arrays.asList("#0.00", "#0,00"), "^(?i)\\s*(true|yes)\\s*$", "^(?i)\\s*(false|no)\\s*$");
    }

    @Bean
    public StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter(List<StringToTypeConverter<?>> stringToTypeConverterList) {
        return new DefaultStringToEntityPropertyMapConverter(stringToTypeConverterList);
    }

    @Bean
    public RegistryConfiguration registryConfiguration() {
        RegistryConfiguration registryConfiguration = new RegistryConfiguration();

        registryConfiguration.setGroupDefinitionConfigurationList(createRegistryGroupDefinitionConfigurationList());
        registryConfiguration.setGroupDisplayOrderList(Arrays.asList("CONFIGURATION", "DATA", "HISTORY"));

        registryConfiguration.setOverrideConfigurationHolderList(createRegistryOverrideConfigurationList());

        return registryConfiguration;
    }

    @Bean
    public RegistryConfigurationResolverService registryConfigurationResolverService(EntityManager entityManager, RegistryConfiguration registryConfiguration) {
        return new DefaultRegistryConfigurationResolverService(entityManager, registryConfiguration);
    }

    @Bean
    public RegistryConfigurationUpdateInterceptor registryConfigurationUpdateInterceptor(RegistryConfigurationResolverService registryConfigurationResolverService) {
        return new RegistryConfigurationUpdateInterceptor(registryConfigurationResolverService.resolveRegistryOverrideConfigurationMap());
    }

    @Bean
    public RegistryConfigurationService registryConfigurationService(MessageSource messageSource, RegistryConfigurationResolverService registryConfigurationResolverService) {
        List<String> defaultReadOnlyPropertyList = Arrays.asList("id", "version");
        RegistryGroupDefinitionHolder registryGroupDefinitionHolder = registryConfigurationResolverService.resolveRegistryGroupDefinition();
        RegistryHistoryConfigurationHolder registryHistoryConfigurationHolder = registryConfigurationResolverService.resolveRegistryHistoryConfiguration();
        Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap = registryConfigurationResolverService.resolveRegistryOverrideConfigurationMap();

        return new DefaultRegistryConfigurationService(messageSource, defaultReadOnlyPropertyList, registryGroupDefinitionHolder, registryHistoryConfigurationHolder, registryOverrideConfigurationMap);
    }

    @Bean
    public RegistryConfigurationController registryConfigurationController(RegistryConfigurationService registryConfigurationService) {
        return new RegistryConfigurationController(registryConfigurationService);
    }

    @Bean
    public RegistryEntityFinderService registryEntityFinderService(EntityManager entityManager, ModelMapper registryBaseModelMapper, RegistryConfigurationResolverService registryConfigurationResolverService) {
        Map<String, ManagedTypeWrapper> managedTypeWrapperMap = registryConfigurationResolverService.resolveRegistryDataConfiguration().getClassNameManagedTypeWrapperMap();

        return new EntityManagerRegistryEntityFinderService(entityManager, registryBaseModelMapper, managedTypeWrapperMap);
    }

    @Bean
    public RegistryDataService registryDataService(EntityManager entityManager, ModelMapper registryDataModelMapper, StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter, RegistryConfigurationResolverService registryConfigurationResolverService, @Autowired(required = false) List<RegistryDataInterceptor> interceptorList, RegistryEntityFinderService registryEntityFinderService) {
        List<RegistryDataInterceptor> interceptors = Optional.ofNullable(interceptorList).orElse(Collections.emptyList());
        RegistryDataConfigurationHolder registryDataConfigurationHolder = registryConfigurationResolverService.resolveRegistryDataConfiguration();

        return new DefaultRegistryDataService(entityManager, registryDataModelMapper, stringToEntityPropertyMapConverter, registryDataConfigurationHolder, interceptors, registryEntityFinderService);
    }

    @Bean
    public RegistryDataRequestConversionService registryDataRequestConversionService(ObjectMapper objectMapper, RegistryConfigurationResolverService registryConfigurationResolverService) {
        return new DefaultRegistryDataRequestConversionService(objectMapper, registryConfigurationResolverService.resolveRegistryDataConfiguration());
    }

    @Bean
    public RegistryDataController registryDataController(RegistryDataService registryDataService, RegistryDataRequestConversionService registryDataRequestConversionService, Validator validator) {
        return new RegistryDataController(registryDataService, registryDataRequestConversionService, validator);
    }

    @Bean
    public RegistryHistoryService registryHistoryService(EntityManager entityManager, RegistryConfigurationResolverService registryConfigurationResolverService, ModelMapper registryBaseModelMapper, RegistryEntityFinderService registryEntityFinderService) {
        RegistryDataConfigurationHolder registryDataConfigurationHolder = registryConfigurationResolverService.resolveRegistryDataConfiguration();
        RegistryHistoryConfigurationHolder historyConfigurationHolder = registryConfigurationResolverService.resolveRegistryHistoryConfiguration();

        return new DefaultRegistryHistoryService(entityManager, registryDataConfigurationHolder, historyConfigurationHolder, registryBaseModelMapper, registryEntityFinderService);
    }

    @Bean
    public RegistryHistoryController registryHistoryController(RegistryHistoryService registryHistoryService) {
        return new RegistryHistoryController(registryHistoryService);
    }

    @Bean
    public RegistryDataFormConfigurationResolverService registryFormConfigurationRegistrationService(RegistryConfigurationResolverService registryConfigurationResolverService) {
        List<Class<?>> registryClassList = registryConfigurationResolverService.resolveRegistryDataConfiguration().getRegistryDataConfigurationList().stream()
            .map(RegistryDataConfiguration::getRegistryType)
            .collect(Collectors.toList());

        Map<String, Class<?>> formConfigurationMap = new HashMap<>();

        formConfigurationMap.put(String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, RegistryTestEntityWithOverriddenFormConfiguration.class.getName(), RegistryDataConstants.REGISTRY_FORM_ID_CREATE_SUFFIX), Object.class);
        formConfigurationMap.put(String.format(RegistryDataConstants.REGISTRY_FORM_ID_FORMAT, RegistryTestEntityWithOverriddenFormConfiguration.class.getName(), RegistryDataConstants.REGISTRY_FORM_ID_UPDATE_SUFFIX), Object.class);

        return new DefaultRegistryDataFormConfigurationResolverService(registryClassList, formConfigurationMap);
    }

    private List<RegistryGroupDefinitionConfiguration> createRegistryGroupDefinitionConfigurationList() {
        RegistryGroupDefinitionConfiguration registryDataConfigurationGroup = new RegistryGroupDefinitionConfiguration();

        registryDataConfigurationGroup.setGroupId("DATA");
        registryDataConfigurationGroup.setIncludeEntityPatternList(Collections.singletonList("^net.croz.nrich.registry.data.stub.*$"));
        registryDataConfigurationGroup.setExcludeEntityPatternList(Collections.singletonList("^net.croz.nrich.registry.data.stub.RegistryTestEmbeddedUserGroupId"));

        RegistryGroupDefinitionConfiguration registryConfigurationConfigurationGroup = new RegistryGroupDefinitionConfiguration();

        registryConfigurationConfigurationGroup.setGroupId("CONFIGURATION");
        registryConfigurationConfigurationGroup.setIncludeEntityPatternList(Collections.singletonList("^net.croz.nrich.registry.configuration.stub.*$"));

        RegistryGroupDefinitionConfiguration registryHistoryConfigurationGroup = new RegistryGroupDefinitionConfiguration();

        registryHistoryConfigurationGroup.setGroupId("HISTORY");
        registryHistoryConfigurationGroup.setIncludeEntityPatternList(Collections.singletonList("^net.croz.nrich.registry.history.stub.*$"));

        return Arrays.asList(registryDataConfigurationGroup, registryConfigurationConfigurationGroup, registryHistoryConfigurationGroup);
    }

    private List<RegistryOverrideConfigurationHolder> createRegistryOverrideConfigurationList() {
        RegistryOverrideConfiguration configurationEntityConfiguration = RegistryOverrideConfiguration.defaultConfiguration();

        configurationEntityConfiguration.setPropertyDisplayOrderList(Arrays.asList("name", "id", "nonEditableProperty", "floatNumber", "doubleNumber"));
        configurationEntityConfiguration.setIgnoredPropertyList(Collections.singletonList("skippedProperty"));
        configurationEntityConfiguration.setNonEditablePropertyList(Collections.singletonList("nonEditableProperty"));
        configurationEntityConfiguration.setNonSortablePropertyList(Collections.singletonList("nonEditableProperty"));
        configurationEntityConfiguration.setDeletable(false);

        RegistryOverrideConfigurationHolder configurationEntityConfigurationHolder = RegistryOverrideConfigurationHolder.builder()
            .type(RegistryConfigurationTestEntity.class).overrideConfiguration(configurationEntityConfiguration).build();

        RegistryOverrideConfiguration interceptorTestEntityConfiguration = RegistryOverrideConfiguration.defaultConfiguration();
        interceptorTestEntityConfiguration.setReadOnly(true);

        RegistryOverrideConfigurationHolder interceptorTestEntityConfigurationHolder = RegistryOverrideConfigurationHolder.builder()
            .type(RegistryConfigurationUpdateInterceptorTestEntity.class).overrideConfiguration(interceptorTestEntityConfiguration).build();

        RegistryOverrideConfiguration InterceptorTestNonEntityNonModifiableConfiguration = RegistryOverrideConfiguration.defaultConfiguration();

        InterceptorTestNonEntityNonModifiableConfiguration.setDeletable(false);
        InterceptorTestNonEntityNonModifiableConfiguration.setUpdateable(false);
        InterceptorTestNonEntityNonModifiableConfiguration.setCreatable(false);

        RegistryOverrideConfigurationHolder InterceptorTestNonEntityNonModifiableConfigurationHolder = RegistryOverrideConfigurationHolder.builder()
            .type(RegistryConfigurationUpdateInterceptorNonModifiableEntity.class).overrideConfiguration(InterceptorTestNonEntityNonModifiableConfiguration).build();

        SearchConfiguration<Object, Object, Map<String, Object>> searchConfiguration = SearchConfiguration.emptyConfiguration();
        searchConfiguration.setSearchOperatorOverrideList(Collections.singletonList(SearchOperatorOverride.forType(String.class, DefaultSearchOperator.EQ)));

        RegistryOverrideConfigurationHolder searchConfigurationHolder = RegistryOverrideConfigurationHolder.builder()
            .type(RegistryTestEntityWithOverriddenSearchConfiguration.class).overrideSearchConfiguration(searchConfiguration).build();

        return Arrays.asList(interceptorTestEntityConfigurationHolder, InterceptorTestNonEntityNonModifiableConfigurationHolder, searchConfigurationHolder, configurationEntityConfigurationHolder);
    }
}
