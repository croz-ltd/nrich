package net.croz.nrich.search;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@Configuration(proxyBeanMethods = false)
public class SearchConfigurationTestConfiguration {

}
