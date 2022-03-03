package net.croz.nrich.search.starter.configuration;

import net.croz.nrich.search.api.converter.StringToEntityPropertyMapConverter;
import net.croz.nrich.search.api.converter.StringToTypeConverter;
import net.croz.nrich.search.api.factory.RepositoryFactorySupportFactory;
import net.croz.nrich.search.starter.properties.NrichSearchProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class NrichSearchAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichSearchAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        // expect
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(StringToTypeConverter.class);
            assertThat(context).hasSingleBean(StringToEntityPropertyMapConverter.class);
            assertThat(context).hasSingleBean(RepositoryFactorySupportFactory.class);
        });
    }

    @Test
    void shouldNotCreateDefaultValueConverterWhenCreationIsDisabled() {
        // expect
        contextRunner.withPropertyValues("nrich.search.default-converter-enabled=false").run(context ->
            assertThat(context).doesNotHaveBean(StringToTypeConverter.class)
        );
    }

    @Test
    void shouldAllowForOverridingStringSearchValues() {
        contextRunner.withPropertyValues("nrich.search.string-search.boolean-true-regex-pattern=new").run(context -> {
            // when
            NrichSearchProperties searchProperties = context.getBean(NrichSearchProperties.class);

            // then
            assertThat(searchProperties.getStringSearch()).isNotNull();
            assertThat(searchProperties.getStringSearch().getBooleanTrueRegexPattern()).isEqualTo("new");
            assertThat(searchProperties.getStringSearch().getBooleanFalseRegexPattern()).isEqualTo("^(?i)\\s*(false|no|ne)\\s*$");
        });
    }
}
