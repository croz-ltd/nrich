package net.croz.nrich.search.starter.configuration;

import net.croz.nrich.search.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.converter.StringToTypeConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(SearchAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(StringToTypeConverter.class);
            assertThat(context).hasSingleBean(StringToEntityPropertyMapConverter.class);
        });
    }

    @Test
    void shouldNotCreateDefaultValueConverterWhenCreationIsDisabled() {
        contextRunner.withPropertyValues("nrich.search.default-converter-enabled=false").run(context -> assertThat(context).doesNotHaveBean(StringToTypeConverter.class));
    }
}
