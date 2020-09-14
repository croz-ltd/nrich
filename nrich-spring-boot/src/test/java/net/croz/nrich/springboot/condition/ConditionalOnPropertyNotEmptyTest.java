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
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(ConditionalOnPropertyNotEmptyConfiguration.StringConditionBean.class);
            assertThat(context).doesNotHaveBean(ConditionalOnPropertyNotEmptyConfiguration.StringListConditionBean.class);
            assertThat(context).doesNotHaveBean(ConditionalOnPropertyNotEmptyConfiguration.MapListConditionBean.class);
        });
    }

    @Test
    void shouldNotRegisterBeansOnEmptyConditions() {
        contextRunner.withPropertyValues("string.condition=", "string.list.condition=", "map.list.condition=").run(context -> {
            assertThat(context).doesNotHaveBean(ConditionalOnPropertyNotEmptyConfiguration.StringConditionBean.class);
            assertThat(context).doesNotHaveBean(ConditionalOnPropertyNotEmptyConfiguration.StringListConditionBean.class);
            assertThat(context).doesNotHaveBean(ConditionalOnPropertyNotEmptyConfiguration.MapListConditionBean.class);
        });
    }

    @Test
    void shouldRegisterBeansOnNotEmptyConditions() {
        contextRunner.withPropertyValues("string.condition=value", "string.list.condition[0]=value", "map.list.condition[0].first=value").run(context -> {
            assertThat(context).hasSingleBean(ConditionalOnPropertyNotEmptyConfiguration.StringConditionBean.class);
            assertThat(context).hasSingleBean(ConditionalOnPropertyNotEmptyConfiguration.StringListConditionBean.class);
            assertThat(context).hasSingleBean(ConditionalOnPropertyNotEmptyConfiguration.MapListConditionBean.class);
        });
    }

}
