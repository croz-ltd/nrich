package net.croz.nrich.springboot.condition.stub;

import net.croz.nrich.springboot.condition.ConditionalOnPropertyNotEmpty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ConditionalOnPropertyNotEmptyConfiguration {

    @ConditionalOnPropertyNotEmpty("string.condition")
    @Bean
    public StringConditionBean stringConditionBean() {
        return new StringConditionBean();
    }

    @ConditionalOnPropertyNotEmpty("string.list.condition")
    @Bean
    public StringListConditionBean stringListConditionBean() {
        return new StringListConditionBean();
    }

    @ConditionalOnPropertyNotEmpty("map.list.condition")
    @Bean
    public MapListConditionBean mapListConditionBean() {
        return new MapListConditionBean();
    }

    public static class StringConditionBean {

    }

    public static class StringListConditionBean {

    }

    public static class MapListConditionBean {

    }
}
