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
