package net.croz.nrich.search.starter.configuration;

import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import net.croz.nrich.search.api.factory.RepositoryFactorySupportFactory;
import net.croz.nrich.search.converter.DefaultStringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.DefaultStringToTypeConverter;
import net.croz.nrich.search.factory.SearchRepositoryFactorySupportFactory;
import net.croz.nrich.search.starter.properties.NrichSearchProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
@EnableConfigurationProperties(NrichSearchProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichSearchAutoConfiguration {

    @ConditionalOnProperty(name = "nrich.search.default-converter-enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(name = "searchDefaultStringToTypeConverter")
    @Bean
    public StringToTypeConverter<Object> searchDefaultStringToTypeConverter(NrichSearchProperties searchProperties) {
        return new DefaultStringToTypeConverter(searchProperties.getStringSearch().getDateFormatList(), searchProperties.getStringSearch().getDecimalNumberFormatList(), searchProperties.getStringSearch().getBooleanTrueRegexPattern(), searchProperties.getStringSearch().getBooleanFalseRegexPattern());
    }

    @ConditionalOnMissingBean(name = "searchStringToEntityPropertyMapConverter")
    @Bean
    public StringToEntityPropertyMapConverter searchStringToEntityPropertyMapConverter(List<StringToTypeConverter<?>> stringToTypeConverterList) {
        return new DefaultStringToEntityPropertyMapConverter(stringToTypeConverterList);
    }

    @ConditionalOnMissingBean
    @Bean
    public RepositoryFactorySupportFactory searchRepositoryFactorySupportFactory(StringToEntityPropertyMapConverter searchStringToEntityPropertyMapConverter) {
        return new SearchRepositoryFactorySupportFactory(searchStringToEntityPropertyMapConverter);
    }
}
