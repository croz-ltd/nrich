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

package net.croz.nrich.registry.starter.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import net.croz.nrich.formconfiguration.api.customizer.FormConfigurationMappingCustomizer;
import net.croz.nrich.javascript.api.converter.JavaToJavascriptTypeConverter;
import net.croz.nrich.javascript.api.service.JavaToJavascriptTypeConversionService;
import net.croz.nrich.javascript.converter.DefaultJavaToJavascriptTypeConverter;
import net.croz.nrich.javascript.service.DefaultJavaToJavascriptTypeConversionService;
import net.croz.nrich.registry.api.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.api.core.customizer.ModelMapperCustomizer;
import net.croz.nrich.registry.api.core.customizer.ModelMapperType;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfigurationHolder;
import net.croz.nrich.registry.api.core.service.RegistryClassResolvingService;
import net.croz.nrich.registry.api.core.service.RegistryEntityFinderService;
import net.croz.nrich.registry.api.data.interceptor.RegistryDataInterceptor;
import net.croz.nrich.registry.api.data.service.RegistryDataService;
import net.croz.nrich.registry.api.enumdata.service.RegistryEnumService;
import net.croz.nrich.registry.api.history.service.RegistryHistoryService;
import net.croz.nrich.registry.configuration.controller.RegistryConfigurationController;
import net.croz.nrich.registry.configuration.service.DefaultRegistryConfigurationService;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryGroupDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;
import net.croz.nrich.registry.core.service.DefaultRegistryClassResolvingService;
import net.croz.nrich.registry.core.service.DefaultRegistryConfigurationResolverService;
import net.croz.nrich.registry.core.service.EntityManagerRegistryEntityFinderService;
import net.croz.nrich.registry.core.service.RegistryConfigurationResolverService;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import net.croz.nrich.registry.data.controller.RegistryDataController;
import net.croz.nrich.registry.data.customizer.RegistryDataFormConfigurationMappingCustomizer;
import net.croz.nrich.registry.data.customizer.RegistryDataModelMapperCustomizer;
import net.croz.nrich.registry.data.service.DefaultRegistryDataRequestConversionService;
import net.croz.nrich.registry.data.service.DefaultRegistryDataService;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import net.croz.nrich.registry.enumdata.controller.RegistryEnumController;
import net.croz.nrich.registry.enumdata.service.DefaultRegistryEnumService;
import net.croz.nrich.registry.history.controller.RegistryHistoryController;
import net.croz.nrich.registry.history.service.DefaultRegistryHistoryService;
import net.croz.nrich.registry.security.interceptor.RegistryConfigurationUpdateInterceptor;
import net.croz.nrich.registry.starter.properties.NrichRegistryProperties;
import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import net.croz.nrich.search.converter.DefaultStringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.DefaultStringToTypeConverter;
import net.croz.nrich.springboot.condition.ConditionalOnPropertyNotEmpty;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AutoConfigureAfter({ ValidationAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ConditionalOnBean(EntityManagerFactory.class)
@ConditionalOnPropertyNotEmpty("nrich.registry.registry-configuration.group-definition-configuration-list")
@EnableConfigurationProperties(NrichRegistryProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichRegistryAutoConfiguration {

    private static final String ENVERS_AUDIT_READER_FACTORY = "org.hibernate.envers.AuditReaderFactory";

    private static final String REGISTRY_CONVERTER = "registry";

    @PersistenceContext
    private EntityManager entityManager;

    @ConditionalOnMissingBean
    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @ConditionalOnProperty(name = "nrich.registry.registry-model-mapper-customizer-enabled", havingValue = "true", matchIfMissing = true)
    @Bean
    public ModelMapperCustomizer registryDataModelMapperCustomizer(@Autowired(required = false) List<RegistryOverrideConfigurationHolder> registryOverrideConfigurationHolderList) {
        return new RegistryDataModelMapperCustomizer(registryOverrideConfigurationHolderList);
    }

    @ConditionalOnMissingBean(name = "registryDataModelMapper")
    @Bean
    public ModelMapper registryDataModelMapper(@Autowired(required = false) List<ModelMapperCustomizer> modelMapperCustomizerList) {
        return strictModelMapper(ModelMapperType.DATA, modelMapperCustomizerList);
    }

    @ConditionalOnMissingBean(name = "registryBaseModelMapper")
    @Bean
    public ModelMapper registryBaseModelMapper(@Autowired(required = false) List<ModelMapperCustomizer> modelMapperCustomizerList) {
        return strictModelMapper(ModelMapperType.BASE, modelMapperCustomizerList);
    }

    @ConditionalOnMissingBean
    @Bean
    public ObjectMapper registryObjectMapper(List<Module> moduleList) {
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

    @ConditionalOnProperty(name = "nrich.registry.default-converter-enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(name = "registryDefaultStringToTypeConverter")
    @Bean
    public StringToTypeConverter<Object> registryDefaultStringToTypeConverter(NrichRegistryProperties registryProperties) {
        return new DefaultStringToTypeConverter(
            registryProperties.registrySearch().dateFormatList(), registryProperties.registrySearch().decimalNumberFormatList(),
            registryProperties.registrySearch().booleanTrueRegexPattern(), registryProperties.registrySearch().booleanFalseRegexPattern()
        );
    }

    @ConditionalOnMissingBean(name = "registryStringToEntityPropertyMapConverter")
    @Bean
    public StringToEntityPropertyMapConverter registryStringToEntityPropertyMapConverter(@Lazy @Autowired(required = false) Map<String, StringToTypeConverter<?>> stringToTypeConverterList) {
        @SuppressWarnings("java:S6204")
        List<StringToTypeConverter<?>> registryConverters = stringToTypeConverterList.entrySet().stream()
            .filter(entry -> entry.getKey().toLowerCase(Locale.ROOT).contains(REGISTRY_CONVERTER))
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());

        return new DefaultStringToEntityPropertyMapConverter(registryConverters);
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryConfigurationResolverService registryConfigurationResolverService(NrichRegistryProperties registryProperties,
                                                                                     @Autowired(required = false) List<RegistryOverrideConfigurationHolder> registryOverrideConfigurationHolderList) {

        List<RegistryOverrideConfigurationHolder> overrideConfigurationHolderList = registryProperties.registryConfiguration().getOverrideConfigurationHolderList();

        if (overrideConfigurationHolderList == null) {
            overrideConfigurationHolderList = new ArrayList<>();
        }
        if (registryOverrideConfigurationHolderList != null) {
            overrideConfigurationHolderList.addAll(registryOverrideConfigurationHolderList);
        }

        registryProperties.registryConfiguration().setOverrideConfigurationHolderList(overrideConfigurationHolderList);

        return new DefaultRegistryConfigurationResolverService(entityManager, registryProperties.registryConfiguration());
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryConfigurationUpdateInterceptor registryConfigurationUpdateInterceptor(RegistryConfigurationResolverService registryConfigurationResolverService) {
        return new RegistryConfigurationUpdateInterceptor(registryConfigurationResolverService.resolveRegistryOverrideConfigurationMap());
    }

    @ConditionalOnProperty(name = "nrich.registry.default-java-to-javascript-converter-enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(name = "registryJavaToJavascriptTypeConverter")
    @Bean
    public JavaToJavascriptTypeConverter registryJavaToJavascriptTypeConverter() {
        return new DefaultJavaToJavascriptTypeConverter();
    }

    @ConditionalOnMissingBean(name = "registryJavaToJavascriptTypeConversionService")
    @Bean
    public JavaToJavascriptTypeConversionService registryJavaToJavascriptTypeConversionService(@Autowired(required = false) List<JavaToJavascriptTypeConverter> converters) {
        return new DefaultJavaToJavascriptTypeConversionService(converters);
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryConfigurationService registryConfigurationService(MessageSource messageSource, RegistryConfigurationResolverService registryConfigurationResolverService,
                                                                     NrichRegistryProperties registryProperties, JavaToJavascriptTypeConversionService registryJavaToJavascriptTypeConversionService) {
        List<String> readOnlyPropertyList = Optional.ofNullable(registryProperties.defaultReadOnlyPropertyList()).orElse(Collections.emptyList());
        RegistryGroupDefinitionHolder registryGroupDefinitionHolder = registryConfigurationResolverService.resolveRegistryGroupDefinition();
        RegistryHistoryConfigurationHolder registryHistoryConfigurationHolder = registryConfigurationResolverService.resolveRegistryHistoryConfiguration();
        Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap = registryConfigurationResolverService.resolveRegistryOverrideConfigurationMap();

        return new DefaultRegistryConfigurationService(
            messageSource, readOnlyPropertyList, registryGroupDefinitionHolder, registryHistoryConfigurationHolder, registryOverrideConfigurationMap, registryJavaToJavascriptTypeConversionService
        );
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean
    @Bean
    public RegistryConfigurationController registryConfigurationController(RegistryConfigurationService registryConfigurationService) {
        return new RegistryConfigurationController(registryConfigurationService);
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryEntityFinderService registryEntityFinderService(ModelMapper registryBaseModelMapper, RegistryConfigurationResolverService registryConfigurationResolverService) {
        Map<String, ManagedTypeWrapper> managedTypeWrapperMap = registryConfigurationResolverService.resolveRegistryDataConfiguration().classNameManagedTypeWrapperMap();

        return new EntityManagerRegistryEntityFinderService(entityManager, registryBaseModelMapper, managedTypeWrapperMap);
    }

    @ConditionalOnMissingBean(RegistryDataService.class)
    @Bean
    public DefaultRegistryDataService registryDataService(ModelMapper registryDataModelMapper, StringToEntityPropertyMapConverter registryStringToEntityPropertyMapConverter,
                                                          RegistryConfigurationResolverService registryConfigurationResolverService,
                                                          @Autowired(required = false) List<RegistryDataInterceptor> interceptorList, RegistryEntityFinderService registryEntityFinderService) {
        RegistryDataConfigurationHolder registryDataConfigurationHolder = registryConfigurationResolverService.resolveRegistryDataConfiguration();
        List<RegistryDataInterceptor> resolvedInterceptorList = Optional.ofNullable(interceptorList).orElse(Collections.emptyList());

        return new DefaultRegistryDataService(
            entityManager, registryDataModelMapper, registryStringToEntityPropertyMapConverter,
            registryDataConfigurationHolder, resolvedInterceptorList, registryEntityFinderService
        );
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryClassResolvingService registryClassResolvingService(RegistryConfigurationResolverService registryConfigurationResolverService, NrichRegistryProperties registryProperties) {
        Map<String, Class<?>> createRegistryClassMapping = registryProperties.registryConfiguration().getCreateRegistryClassMapping();
        Map<String, Class<?>> updateRegistryClassMapping = registryProperties.registryConfiguration().getUpdateRegistryClassMapping();

        return new DefaultRegistryClassResolvingService(registryConfigurationResolverService.resolveRegistryDataConfiguration(), createRegistryClassMapping, updateRegistryClassMapping);
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryDataRequestConversionService registryDataRequestConversionService(ObjectMapper objectMapper, RegistryClassResolvingService registryClassResolvingService) {
        return new DefaultRegistryDataRequestConversionService(objectMapper, registryClassResolvingService);
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean
    @Bean
    public RegistryDataController registryDataController(RegistryDataService registryDataService, RegistryDataRequestConversionService registryDataRequestConversionService, Validator validator) {
        return new RegistryDataController(registryDataService, registryDataRequestConversionService, validator);
    }

    @ConditionalOnClass(name = ENVERS_AUDIT_READER_FACTORY)
    @ConditionalOnMissingBean(RegistryHistoryService.class)
    @Bean
    public DefaultRegistryHistoryService registryHistoryService(RegistryConfigurationResolverService registryConfigurationResolverService, ModelMapper registryBaseModelMapper,
                                                                RegistryEntityFinderService registryEntityFinderService) {
        RegistryDataConfigurationHolder registryDataConfigurationHolder = registryConfigurationResolverService.resolveRegistryDataConfiguration();
        RegistryHistoryConfigurationHolder registryHistoryConfigurationHolder = registryConfigurationResolverService.resolveRegistryHistoryConfiguration();

        return new DefaultRegistryHistoryService(entityManager, registryDataConfigurationHolder, registryHistoryConfigurationHolder, registryBaseModelMapper, registryEntityFinderService);
    }

    @ConditionalOnClass(name = ENVERS_AUDIT_READER_FACTORY)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean
    @Bean
    public RegistryHistoryController registryHistoryController(RegistryHistoryService registryHistoryService) {
        return new RegistryHistoryController(registryHistoryService);
    }

    @ConditionalOnMissingBean(name = "registryDataFormConfigurationMappingCustomizer")
    @Bean
    public FormConfigurationMappingCustomizer registryDataFormConfigurationMappingCustomizer(RegistryConfigurationResolverService registryConfigurationResolverService,
                                                                                             RegistryClassResolvingService registryClassResolvingService) {
        @SuppressWarnings("java:S6204")
        List<Class<?>> registryClassList = registryConfigurationResolverService.resolveRegistryDataConfiguration().registryDataConfigurationList().stream()
            .map(RegistryDataConfiguration::registryType)
            .collect(Collectors.toList());

        return new RegistryDataFormConfigurationMappingCustomizer(registryClassResolvingService, registryClassList);
    }

    @ConditionalOnMissingBean(RegistryEnumService.class)
    @Bean
    public DefaultRegistryEnumService registryEnumService(MessageSource messageSource) {
        return new DefaultRegistryEnumService(messageSource);
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean
    @Bean
    public RegistryEnumController registryEnumController(RegistryEnumService registryEnumService) {
        return new RegistryEnumController(registryEnumService);
    }

    protected ModelMapper strictModelMapper(ModelMapperType type, List<ModelMapperCustomizer> modelMapperCustomizerList) {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        if (!CollectionUtils.isEmpty(modelMapperCustomizerList)) {
            modelMapperCustomizerList.forEach(modelMapperCustomizer -> modelMapperCustomizer.customize(type, modelMapper));
        }

        return modelMapper;
    }
}
