package net.croz.nrich.registry.starter.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import net.croz.nrich.registry.api.model.RegistryConfiguration;
import net.croz.nrich.registry.configuration.controller.RegistryConfigurationController;
import net.croz.nrich.registry.configuration.service.DefaultRegistryConfigurationService;
import net.croz.nrich.registry.configuration.service.RegistryConfigurationService;
import net.croz.nrich.registry.core.model.RegistryDataConfiguration;
import net.croz.nrich.registry.core.service.DefaultRegistryConfigurationResolverService;
import net.croz.nrich.registry.core.service.RegistryConfigurationResolverService;
import net.croz.nrich.registry.data.controller.RegistryDataController;
import net.croz.nrich.registry.data.interceptor.RegistryDataInterceptor;
import net.croz.nrich.registry.data.service.DefaultRegistryDataFormConfigurationResolverService;
import net.croz.nrich.registry.data.service.DefaultRegistryDataRequestConversionService;
import net.croz.nrich.registry.data.service.DefaultRegistryDataService;
import net.croz.nrich.registry.data.service.RegistryDataFormConfigurationResolverService;
import net.croz.nrich.registry.data.service.RegistryDataRequestConversionService;
import net.croz.nrich.registry.data.service.RegistryDataService;
import net.croz.nrich.registry.history.controller.RegistryHistoryController;
import net.croz.nrich.registry.history.service.DefaultRegistryHistoryService;
import net.croz.nrich.registry.history.service.RegistryHistoryService;
import net.croz.nrich.registry.security.interceptor.RegistryConfigurationUpdateInterceptor;
import net.croz.nrich.registry.starter.properties.NrichRegistryProperties;
import net.croz.nrich.search.converter.DefaultStringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.DefaultStringToTypeConverter;
import net.croz.nrich.search.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.StringToTypeConverter;
import org.modelmapper.Condition;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public static final String FORM_CONFIGURATION_MAPPING_BEAN_NAME = "formConfigurationMapping";

    private static final String ENVERS_AUDIT_READER_FACTORY = "org.hibernate.envers.AuditReaderFactory";

    @PersistenceContext
    private EntityManager entityManager;

    @ConditionalOnMissingBean(name = "registryDataModelMapper")
    @Bean
    public ModelMapper registryDataModelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        final Condition<Object, Object> skipIds = context -> !context.getMapping().getLastDestinationProperty().getName().equals("id");

        modelMapper.getConfiguration().setPropertyCondition(skipIds);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }

    @ConditionalOnMissingBean(name = "registryHistoryModelMapper")
    @Bean
    public ModelMapper registryHistoryModelMapper() {
        final ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }

    @ConditionalOnMissingBean
    @Bean
    public ObjectMapper registryObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

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
    public StringToTypeConverter<?> registryDefaultStringToTypeConverter(final NrichRegistryProperties registryProperties) {
        return new DefaultStringToTypeConverter(registryProperties.getRegistrySearch().getDateFormatList(), registryProperties.getRegistrySearch().getDecimalNumberFormatList(), registryProperties.getRegistrySearch().getBooleanTrueRegexPattern(), registryProperties.getRegistrySearch().getBooleanFalseRegexPattern());
    }

    @ConditionalOnMissingBean(name = "registryStringToEntityPropertyMapConverter")
    @Bean
    public StringToEntityPropertyMapConverter registryStringToEntityPropertyMapConverter(final List<StringToTypeConverter<?>> stringToTypeConverterList) {
        return new DefaultStringToEntityPropertyMapConverter(stringToTypeConverterList);
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryConfigurationResolverService registryConfigurationResolverService(final RegistryConfiguration registryConfiguration) {
        return new DefaultRegistryConfigurationResolverService(entityManager, registryConfiguration);
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryConfigurationUpdateInterceptor registryConfigurationUpdateInterceptor(final RegistryConfigurationResolverService registryConfigurationResolverService) {
        return new RegistryConfigurationUpdateInterceptor(registryConfigurationResolverService.resolveRegistryOverrideConfigurationMap());
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryConfigurationService registryConfigurationService(final MessageSource messageSource, final RegistryConfigurationResolverService registryConfigurationResolverService, final NrichRegistryProperties registryProperties) {
        return new DefaultRegistryConfigurationService(messageSource, registryProperties.getDefaultReadOnlyPropertyList(), registryConfigurationResolverService.resolveRegistryGroupDefinition(), registryConfigurationResolverService.resolveRegistryHistoryConfiguration(), registryConfigurationResolverService.resolveRegistryOverrideConfigurationMap());
    }

    @ConditionalOnWebApplication
    @Bean
    public RegistryConfigurationController registryConfigurationController(final RegistryConfigurationService registryConfigurationService) {
        return new RegistryConfigurationController(registryConfigurationService);
    }

    @Bean
    public RegistryDataService registryDataService(final ModelMapper registryDataModelMapper, final StringToEntityPropertyMapConverter stringToEntityPropertyMapConverter, final RegistryConfigurationResolverService registryConfigurationResolverService, @Autowired(required = false) final List<RegistryDataInterceptor> interceptorList) {
        return new DefaultRegistryDataService(entityManager, registryDataModelMapper, stringToEntityPropertyMapConverter, registryConfigurationResolverService.resolveRegistryDataConfiguration(), Optional.ofNullable(interceptorList).orElse(Collections.emptyList()));
    }

    @ConditionalOnMissingBean
    @Bean
    public RegistryDataRequestConversionService registryDataRequestConversionService(final ObjectMapper objectMapper, final RegistryConfigurationResolverService registryConfigurationResolverService) {
        return new DefaultRegistryDataRequestConversionService(objectMapper, registryConfigurationResolverService.resolveRegistryDataConfiguration());
    }

    @ConditionalOnWebApplication
    @Bean
    public RegistryDataController registryDataController(final RegistryDataService registryDataService, final RegistryDataRequestConversionService registryDataRequestConversionService, final Validator validator) {
        return new RegistryDataController(registryDataService, registryDataRequestConversionService, validator);
    }

    @ConditionalOnClass(name = ENVERS_AUDIT_READER_FACTORY)
    @Bean
    public RegistryHistoryService registryHistoryService(final RegistryConfigurationResolverService registryConfigurationResolverService, final ModelMapper registryHistoryModelMapper) {
        return new DefaultRegistryHistoryService(entityManager, registryConfigurationResolverService.resolveRegistryDataConfiguration(), registryConfigurationResolverService.resolveRegistryHistoryConfiguration(), registryHistoryModelMapper);
    }

    @ConditionalOnClass(name = ENVERS_AUDIT_READER_FACTORY)
    @ConditionalOnWebApplication
    @Bean
    public RegistryHistoryController registryHistoryController(final RegistryHistoryService registryHistoryService) {
        return new RegistryHistoryController(registryHistoryService);
    }

    @ConditionalOnBean(name = FORM_CONFIGURATION_MAPPING_BEAN_NAME)
    @Bean
    public RegistryDataFormConfigurationResolverService registryFormConfigurationRegistrationService(final RegistryConfigurationResolverService registryConfigurationResolverService, @Qualifier(FORM_CONFIGURATION_MAPPING_BEAN_NAME) final Map<String, Class<?>> formConfigurationMapping) {
        final List<Class<?>> registryClassList = registryConfigurationResolverService.resolveRegistryDataConfiguration().getRegistryDataConfigurationList().stream()
                .map(RegistryDataConfiguration::getRegistryType)
                .collect(Collectors.toList());

        return new DefaultRegistryDataFormConfigurationResolverService(registryClassList, formConfigurationMapping);
    }
}
