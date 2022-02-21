package net.croz.nrich.excel.starter.configuration;

import net.croz.nrich.excel.api.converter.CellValueConverter;
import net.croz.nrich.excel.api.generator.ExcelReportGeneratorFactory;
import net.croz.nrich.excel.api.service.ExcelReportService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class NrichExcelAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(NrichExcelAutoConfiguration.class));

    @Test
    void shouldConfigureDefaultConfiguration() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CellValueConverter.class);
            assertThat(context).hasSingleBean(ExcelReportGeneratorFactory.class);
            assertThat(context).hasSingleBean(ExcelReportService.class);
        });
    }

    @Test
    void shouldNotCreateDefaultValueConverterWhenCreationIsDisabled() {
        contextRunner.withPropertyValues("nrich.excel.default-converter-enabled=false").run(context -> assertThat(context).doesNotHaveBean(CellValueConverter.class));
    }
}
