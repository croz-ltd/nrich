/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

package net.croz.nrich.jackson.starter.configuration;

import com.fasterxml.jackson.databind.Module;
import net.croz.nrich.jackson.module.JacksonModuleUtil;
import net.croz.nrich.jackson.starter.properties.NrichJacksonProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// TODO this module is not really a starter think of a better name, maybe just remove starter suffix?
@PropertySource("classpath:nrich-jackson.properties")
@AutoConfigureAfter(JacksonAutoConfiguration.class)
@EnableConfigurationProperties(NrichJacksonProperties.class)
@Configuration(proxyBeanMethods = false)
public class NrichJacksonAutoConfiguration {

    @ConditionalOnProperty(name = "nrich.jackson.convert-empty-strings-to-null", havingValue = "true", matchIfMissing = true)
    @Bean
    public Module convertEmptyStringsToNullModule() {
        return JacksonModuleUtil.convertEmptyStringToNullModule();
    }

    @ConditionalOnProperty(name = "nrich.jackson.serialize-class-name", havingValue = "true", matchIfMissing = true)
    @Bean
    public Module classNameSerializerModule(NrichJacksonProperties nrichJacksonProperties) {
        return JacksonModuleUtil.classNameSerializerModule(
            nrichJacksonProperties.isSerializeClassNameForEntityAnnotatedClasses(), nrichJacksonProperties.getAdditionalPackageListForClassNameSerialization()
        );
    }
}
