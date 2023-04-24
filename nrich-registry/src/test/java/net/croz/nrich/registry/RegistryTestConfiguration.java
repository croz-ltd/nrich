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

package net.croz.nrich.registry;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import net.croz.nrich.javascript.api.service.JavaToJavascriptTypeConversionService;
import net.croz.nrich.javascript.converter.DefaultJavaToJavascriptTypeConverter;
import net.croz.nrich.javascript.service.DefaultJavaToJavascriptTypeConversionService;
import net.croz.nrich.registry.api.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.api.core.model.RegistryConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryGroupDefinitionConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfigurationHolder;
import net.croz.nrich.registry.api.core.service.RegistryClassResolvingService;
import net.croz.nrich.registry.api.core.service.RegistryEntityFinderService;
import net.croz.nrich.registry.api.data.interceptor.RegistryDataInterceptor;
import net.croz.nrich.registry.api.data.service.RegistryDataService;
import net.croz.nrich.registry.api.history.service.RegistryHistoryService;
import net.croz.nrich.registry.configuration.controller.RegistryConfigurationController;
import net.croz.nrich.registry.configuration.service.DefaultRegistryConfigurationService;
import net.croz.nrich.registry.configuration.stub.RegistryConfigurationTestEntity;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryGroupDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;
import net.croz.nrich.registry.core.service.DefaultRegistryClassResolvingService;
import net.croz.nrich.registry.core.service.DefaultRegistryConfigurationResolverService;
import net.croz.nrich.registry.core.service.EntityManagerRegistryEntityFinderService;
import net.croz.nrich.registry.core.service.RegistryConfigurationResolverService;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import net.croz.nrich.registry.data.controller.RegistryDataController;
import net.croz.nrich.registry.data.service.DefaultRegistryDataRequestConversionService;
import net.croz.nrich.registry.data.service.DefaultRegistryDataService;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        return strictModelMapper();
    }

    @Bean
    public ModelMapper registryBaseModelMapper() {
        return strictModelMapper();
    }

    @Bean
    public ObjectMapper objectMapper(List<Module> moduleList) {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);

        objectMapper.setDateFormat(new StdDateFormat());

        objectMapper.registerModules(moduleList);

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
    public StringToTypeConverter<Object> defaultStringToTypeConverter() {
        List<String> dateFormatList = Arrays.asList("dd.MM.yyyy.", "dd.MM.yyyy.'T'HH:mm", "dd.MM.yyyy.'T'HH:mm'Z'");
        List<String> decimalFormatList = Arrays.asList("#0.00", "#0,00");
        String booleanTrueRegexPattern = "^(?i)\\s*(true|yes)\\s*$";
        String booleanFalseRegexPattern = "^(?i)\\s*(false|no)\\s*$";

        return new DefaultStringToTypeConverter(dateFormatList, decimalFormatList, booleanTrueRegexPattern, booleanFalseRegexPattern);
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
    public JavaToJavascriptTypeConversionService javaToJavascriptTypeConversionService() {
        return new DefaultJavaToJavascriptTypeConversionService(Collections.singletonList(new DefaultJavaToJavascriptTypeConverter()));
    }

    @Bean
    public RegistryConfigurationService registryConfigurationService(MessageSource messageSource, RegistryConfigurationResolverService registryConfigurationResolverService,
                                                                     JavaToJavascriptTypeConversionService javaToJavascriptTypeConversionService) {
        List<String> defaultReadOnlyPropertyList = Arrays.asList("id", "version");
        RegistryGroupDefinitionHolder registryGroupDefinitionHolder = registryConfigurationResolverService.resolveRegistryGroupDefinition();
        RegistryHistoryConfigurationHolder registryHistoryConfigurationHolder = registryConfigurationResolverService.resolveRegistryHistoryConfiguration();
        Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap = registryConfigurationResolverService.resolveRegistryOverrideConfigurationMap();

        return new DefaultRegistryConfigurationService(
            messageSource, defaultReadOnlyPropertyList, registryGroupDefinitionHolder, registryHistoryConfigurationHolder, registryOverrideConfigurationMap, javaToJavascriptTypeConversionService
        );
    }

    @Bean
    public RegistryConfigurationController registryConfigurationController(RegistryConfigurationService registryConfigurationService) {
        return new RegistryConfigurationController(registryConfigurationService);
    }

    @Bean
    public RegistryEntityFinderService registryEntityFinderService(EntityManager entityManager, ModelMapper registryBaseModelMapper,
                                                                   RegistryConfigurationResolverService registryConfigurationResolverService) {
        Map<String, ManagedTypeWrapper> managedTypeWrapperMap = registryConfigurationResolverService.resolveRegistryDataConfiguration().getClassNameManagedTypeWrapperMap();

        return new EntityManagerRegistryEntityFinderService(entityManager, registryBaseModelMapper, managedTypeWrapperMap);
    }

    @Bean
    public RegistryClassResolvingService registryClassResolvingService(RegistryConfigurationResolverService registryConfigurationResolverService) {
        return new DefaultRegistryClassResolvingService(registryConfigurationResolverService.resolveRegistryDataConfiguration(), Collections.emptyMap(), Collections.emptyMap());
    }

    @Bean
    public RegistryDataService registryDataService(EntityManager entityManager, ModelMapper registryDataModelMapper, StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter,
                                                   RegistryConfigurationResolverService registryConfigurationResolverService,
                                                   @Autowired(required = false) List<RegistryDataInterceptor> interceptorList, RegistryEntityFinderService registryEntityFinderService) {
        List<RegistryDataInterceptor> interceptors = Optional.ofNullable(interceptorList).orElse(Collections.emptyList());
        RegistryDataConfigurationHolder registryDataConfigurationHolder = registryConfigurationResolverService.resolveRegistryDataConfiguration();

        return new DefaultRegistryDataService(entityManager, registryDataModelMapper, stringToEntityPropertyMapConverter, registryDataConfigurationHolder, interceptors, registryEntityFinderService);
    }

    @Bean
    public RegistryDataRequestConversionService registryDataRequestConversionService(ObjectMapper objectMapper, RegistryClassResolvingService registryClassResolvingService) {
        return new DefaultRegistryDataRequestConversionService(objectMapper, registryClassResolvingService);
    }

    @Bean
    public RegistryDataController registryDataController(RegistryDataService registryDataService, RegistryDataRequestConversionService registryDataRequestConversionService, Validator validator) {
        return new RegistryDataController(registryDataService, registryDataRequestConversionService, validator);
    }

    @Bean
    public RegistryHistoryService registryHistoryService(EntityManager entityManager, RegistryConfigurationResolverService registryConfigurationResolverService, ModelMapper registryBaseModelMapper,
                                                         RegistryEntityFinderService registryEntityFinderService) {
        RegistryDataConfigurationHolder registryDataConfigurationHolder = registryConfigurationResolverService.resolveRegistryDataConfiguration();
        RegistryHistoryConfigurationHolder historyConfigurationHolder = registryConfigurationResolverService.resolveRegistryHistoryConfiguration();

        return new DefaultRegistryHistoryService(entityManager, registryDataConfigurationHolder, historyConfigurationHolder, registryBaseModelMapper, registryEntityFinderService);
    }

    @Bean
    public RegistryHistoryController registryHistoryController(RegistryHistoryService registryHistoryService) {
        return new RegistryHistoryController(registryHistoryService);
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

    private ModelMapper strictModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }
}
