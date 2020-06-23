package net.croz.nrich.search.starter.configuration;

import net.croz.nrich.search.converter.DefaultStringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.DefaultStringToTypeConverter;
import net.croz.nrich.search.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.StringToTypeConverter;
import net.croz.nrich.search.factory.SearchExecutorJpaRepositoryFactoryBean;
import net.croz.nrich.search.starter.properties.SearchProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableConfigurationProperties(SearchProperties.class)
@EnableJpaRepositories(repositoryFactoryBeanClass = SearchExecutorJpaRepositoryFactoryBean.class)
@Configuration(proxyBeanMethods = false)
public class SearchAutoConfiguration {

    @ConditionalOnProperty(name = "nrich.search.default-converter-enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(name = "searchDefaultStringToTypeConverter")
    @Bean
    public StringToTypeConverter<?> searchDefaultStringToTypeConverter(final SearchProperties searchProperties) {
        return new DefaultStringToTypeConverter(searchProperties.getDateFormatList(), searchProperties.getDecimalNumberFormatList(), searchProperties.getBooleanTrueRegexPattern(), searchProperties.getBooleanFalseRegexPattern());
    }

    @ConditionalOnMissingBean(name = "searchStringToEntityPropertyMapConverter")
    @Bean
    public StringToEntityPropertyMapConverter searchStringToEntityPropertyMapConverter(final List<StringToTypeConverter<?>> stringToTypeConverterList) {
        return new DefaultStringToEntityPropertyMapConverter(stringToTypeConverterList);
    }
}
