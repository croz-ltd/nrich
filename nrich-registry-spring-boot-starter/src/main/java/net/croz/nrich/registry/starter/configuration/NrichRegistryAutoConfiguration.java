package net.croz.nrich.registry.starter.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import net.croz.nrich.formconfiguration.api.customizer.FormConfigurationMappingCustomizer;
import net.croz.nrich.registry.api.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.api.core.model.RegistryConfiguration;
import net.croz.nrich.registry.api.core.model.RegistryOverrideConfiguration;
import net.croz.nrich.registry.api.core.service.RegistryEntityFinderService;
import net.croz.nrich.registry.api.data.interceptor.RegistryDataInterceptor;
import net.croz.nrich.registry.api.data.service.RegistryDataService;
import net.croz.nrich.registry.api.history.service.RegistryHistoryService;
import net.croz.nrich.registry.configuration.controller.RegistryConfigurationController;
import net.croz.nrich.registry.configuration.service.DefaultRegistryConfigurationService;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.model.RegistryDataConfigurationHolder;
import net.croz.nrich.registry.core.model.RegistryGroupDefinitionHolder;
import net.croz.nrich.registry.core.model.RegistryHistoryConfigurationHolder;
import net.croz.nrich.registry.core.service.DefaultRegistryConfigurationResolverService;
import net.croz.nrich.registry.core.service.EntityManagerRegistryEntityFinderService;
import net.croz.nrich.registry.core.service.RegistryConfigurationResolverService;
import net.croz.nrich.registry.core.support.ManagedTypeWrapper;
import net.croz.nrich.registry.data.controller.RegistryDataController;
import net.croz.nrich.registry.data.customizer.RegistryDataFormConfigurationMappingCustomizer;
import net.croz.nrich.registry.data.service.DefaultRegistryDataRequestConversionService;
import net.croz.nrich.registry.data.service.DefaultRegistryDataService;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import net.croz.nrich.registry.history.controller.RegistryHistoryController;
import net.croz.nrich.registry.history.service.DefaultRegistryHistoryService;
import net.croz.nrich.registry.security.interceptor.RegistryConfigurationUpdateInterceptor;
import net.croz.nrich.registry.starter.properties.NrichRegistryProperties;
import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import net.croz.nrich.search.converter.DefaultStringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.DefaultStringToTypeConverter;
import org.modelmapper.Condition;
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
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AutoConfigureAfter({ ValidationAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@ConditionalOnBean({ EntityManagerFactory.class, LocalValidatorFactoryBean.class, RegistryConfiguration.class })
@EnableConfigurationProperties(NrichRegistryProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichRegistryAutoConfiguration {

    private static final String ENVERS_AUDIT_READER_FACTORY = "org.hibernate.envers.AuditReaderFactory";

    @PersistenceContext
    private EntityManager entityManager;

    @ConditionalOnMissingBean(name = "registryDataModelMapper")
    @Bean
    public ModelMapper registryDataModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        Condition<Object, Object> skipIds = context -> !context.getMapping().getLastDestinationProperty().getName().equals("id");

        modelMapper.getConfiguration().setPropertyCondition(skipIds);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }

    @ConditionalOnMissingBean(name = "registryBaseModelMapper")
    @Bean
    public ModelMapper registryBaseModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }

    @ConditionalOnMissingBean
    @Bean
    public ObjectMapper registryObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.findAndRegisterModules();

        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);

        objectMapper.setDateFormat(new StdDateFormat());

        return objectMapper;
    }

    @ConditionalOnProperty(name = "nrich.registry.default-converter-enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(name = "registryDefaultStringToTypeConverter")
    @Bean
    public StringToTypeConverter<Object> registryDefaultStringToTypeConverter(NrichRegistryProperties registryProperties) {
        return new DefaultStringToTypeConverter(
            registryProperties.getRegistrySearch().getDateFormatList(), registryProperties.getRegistrySearch().getDecimalNumberFormatList(),
            registryProperties.getRegistrySearch().getBooleanTrueRegexPattern(), registryProperties.getRegistrySearch().getBooleanFalseRegexPattern()
        );
    }

    // TODO qualifier on a list, prefix maybe, this way it will pick up from search also?
    @ConditionalOnMissingBean(name = "registryStringToEntityPropertyMapConverter")
    @Bean
    public StringToEntityPropertyMapConverter registryStringToEntityPropertyMapConverter(List<StringToTypeConverter<?>> stringToTypeConverterList) {
        return new DefaultStringToEntityPropertyMapConverter(stringToTypeConverterList);
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryConfigurationResolverService registryConfigurationResolverService(RegistryConfiguration registryConfiguration) {
        return new DefaultRegistryConfigurationResolverService(entityManager, registryConfiguration);
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryConfigurationUpdateInterceptor registryConfigurationUpdateInterceptor(RegistryConfigurationResolverService registryConfigurationResolverService) {
        return new RegistryConfigurationUpdateInterceptor(registryConfigurationResolverService.resolveRegistryOverrideConfigurationMap());
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryConfigurationService registryConfigurationService(MessageSource messageSource, RegistryConfigurationResolverService registryConfigurationResolverService,
                                                                     NrichRegistryProperties registryProperties) {
        List<String> readOnlyPropertyList = Optional.ofNullable(registryProperties.getDefaultReadOnlyPropertyList()).orElse(Collections.emptyList());
        RegistryGroupDefinitionHolder registryGroupDefinitionHolder = registryConfigurationResolverService.resolveRegistryGroupDefinition();
        RegistryHistoryConfigurationHolder registryHistoryConfigurationHolder = registryConfigurationResolverService.resolveRegistryHistoryConfiguration();
        Map<Class<?>, RegistryOverrideConfiguration> registryOverrideConfigurationMap = registryConfigurationResolverService.resolveRegistryOverrideConfigurationMap();

        return new DefaultRegistryConfigurationService(messageSource, readOnlyPropertyList, registryGroupDefinitionHolder, registryHistoryConfigurationHolder, registryOverrideConfigurationMap);
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
        Map<String, ManagedTypeWrapper> managedTypeWrapperMap = registryConfigurationResolverService.resolveRegistryDataConfiguration().getClassNameManagedTypeWrapperMap();

        return new EntityManagerRegistryEntityFinderService(entityManager, registryBaseModelMapper, managedTypeWrapperMap);
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryDataService registryDataService(ModelMapper registryDataModelMapper, StringToEntityPropertyMapConverter registryStringToEntityPropertyMapConverter,
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
    public RegistryDataRequestConversionService registryDataRequestConversionService(ObjectMapper objectMapper, RegistryConfigurationResolverService registryConfigurationResolverService) {
        RegistryDataConfigurationHolder registryDataConfigurationHolder = registryConfigurationResolverService.resolveRegistryDataConfiguration();

        return new DefaultRegistryDataRequestConversionService(objectMapper, registryDataConfigurationHolder);
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean
    @Bean
    public RegistryDataController registryDataController(RegistryDataService registryDataService, RegistryDataRequestConversionService registryDataRequestConversionService, Validator validator) {
        return new RegistryDataController(registryDataService, registryDataRequestConversionService, validator);
    }

    @ConditionalOnClass(name = ENVERS_AUDIT_READER_FACTORY)
    @ConditionalOnMissingBean
    @Bean
    public RegistryHistoryService registryHistoryService(RegistryConfigurationResolverService registryConfigurationResolverService, ModelMapper registryBaseModelMapper,
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
    public FormConfigurationMappingCustomizer registryDataFormConfigurationMappingCustomizer(RegistryConfigurationResolverService registryConfigurationResolverService) {
        List<Class<?>> registryClassList = registryConfigurationResolverService.resolveRegistryDataConfiguration().getRegistryDataConfigurationList().stream()
            .map(RegistryDataConfiguration::getRegistryType)
            .collect(Collectors.toList());

        return new RegistryDataFormConfigurationMappingCustomizer(registryClassList);
    }
}
