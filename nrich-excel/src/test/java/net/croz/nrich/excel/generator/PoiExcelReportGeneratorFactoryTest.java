package net.croz.nrich.excel.generator;

import net.croz.nrich.excel.ExcelTestConfiguration;
import net.croz.nrich.excel.api.generator.ExcelReportGenerator;
import net.croz.nrich.excel.api.request.CreateReportGeneratorRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(ExcelTestConfiguration.class)
class PoiExcelReportGeneratorFactoryTest {

    @Autowired
    private PoiExcelReportGeneratorFactory excelReportGeneratorFactory;

    @Test
    void shouldThrowExceptionWhenOutputFileDoesntExist() {
        // given
        CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().build();

        // when
        Throwable thrown = catchThrowable(() -> excelReportGeneratorFactory.createReportGenerator(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("OutputStream cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenTemplateFileDoesntExist() {
        // given
        OutputStream outputStream = new ByteArrayOutputStream();
        CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().outputStream(outputStream).build();

        // when
        Throwable thrown = catchThrowable(() -> excelReportGeneratorFactory.createReportGenerator(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Template path cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenRowIndexIsNegative() {
        // given
        OutputStream outputStream = new ByteArrayOutputStream();
        CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().outputStream(outputStream).templatePath("classpath:excel/template.xlsx").firstRowIndex(-1).build();

        // when
        Throwable thrown = catchThrowable(() -> excelReportGeneratorFactory.createReportGenerator(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Row index must be greater or equal to 0");
    }

    @Test
    void shouldReturnReportGeneratorWhenDataIsValid() {
        // given
        OutputStream outputStream = new ByteArrayOutputStream();
        CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().outputStream(outputStream).templatePath("classpath:excel/template.xlsx").firstRowIndex(1).build();

        // when
        ExcelReportGenerator excelReportGenerator = excelReportGeneratorFactory.createReportGenerator(request);

        // then
        assertThat(excelReportGenerator).isNotNull();
    }
}
