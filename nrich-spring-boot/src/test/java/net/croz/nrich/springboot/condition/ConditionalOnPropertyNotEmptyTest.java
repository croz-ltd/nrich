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

package net.croz.nrich.springboot.condition;

import net.croz.nrich.springboot.condition.stub.ConditionalOnPropertyNotEmptyConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ConditionalOnPropertyNotEmptyTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(ConditionalOnPropertyNotEmptyConfiguration.class));

    @Test
    void shouldNotRegisterBeansOnNullConditions() {
        // expect
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(ConditionalOnPropertyNotEmptyConfiguration.StringConditionBean.class);
            assertThat(context).doesNotHaveBean(ConditionalOnPropertyNotEmptyConfiguration.StringListConditionBean.class);
            assertThat(context).doesNotHaveBean(ConditionalOnPropertyNotEmptyConfiguration.MapListConditionBean.class);
        });
    }

    @Test
    void shouldNotRegisterBeansOnEmptyConditions() {
        // expect
        contextRunner.withPropertyValues("string.condition=", "string.list.condition=", "map.list.condition=").run(context -> {
            assertThat(context).doesNotHaveBean(ConditionalOnPropertyNotEmptyConfiguration.StringConditionBean.class);
            assertThat(context).doesNotHaveBean(ConditionalOnPropertyNotEmptyConfiguration.StringListConditionBean.class);
            assertThat(context).doesNotHaveBean(ConditionalOnPropertyNotEmptyConfiguration.MapListConditionBean.class);
        });
    }

    @Test
    void shouldRegisterBeansOnNotEmptyConditions() {
        // expect
        contextRunner.withPropertyValues("string.condition=value", "string.list.condition[0]=value", "map.list.condition[0].first=value").run(context -> {
            assertThat(context).hasSingleBean(ConditionalOnPropertyNotEmptyConfiguration.StringConditionBean.class);
            assertThat(context).hasSingleBean(ConditionalOnPropertyNotEmptyConfiguration.StringListConditionBean.class);
            assertThat(context).hasSingleBean(ConditionalOnPropertyNotEmptyConfiguration.MapListConditionBean.class);
        });
    }
}
