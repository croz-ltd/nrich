package net.croz.nrich.excel.generator;

import lombok.SneakyThrows;
import net.croz.nrich.excel.ExcelTestConfiguration;
import net.croz.nrich.excel.api.generator.ExcelReportGenerator;
import net.croz.nrich.excel.api.request.CreateReportGeneratorRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.Assert;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(ExcelTestConfiguration.class)
class PoiExcelReportGeneratorFactoryTest {

    @Autowired
    private PoiExcelReportGeneratorFactory excelReportGeneratorFactory;

    @TempDir
    File temporaryDirectory;

    @Test
    void shouldThrowExceptionWhenOutputFileDoesntExist() {
        // given
        CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().build();

        // when
        Throwable thrown = catchThrowable(() -> excelReportGeneratorFactory.createReportGenerator(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenTemplateFileDoesntExist() {
        // given
        CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().outputFile(new File(temporaryDirectory, "output.xlsx")).build();

        // when
        Throwable thrown = catchThrowable(() -> excelReportGeneratorFactory.createReportGenerator(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenRowIndexIsNegative() {
        // given
        File file = createFileInTemporaryDirectory("missing-row.xlxs");

        CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().outputFile(file).templatePath("classpath:excel/template.xlsx").firstRowIndex(-1).build();

        // when
        Throwable thrown = catchThrowable(() -> excelReportGeneratorFactory.createReportGenerator(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldReturnReportGeneratorWhenDataIsValid() {
        // given
        File file = createFileInTemporaryDirectory("valid.xlxs");

        CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().outputFile(file).templatePath("classpath:excel/template.xlsx").firstRowIndex(1).build();

        // when
        ExcelReportGenerator excelReportGenerator = excelReportGeneratorFactory.createReportGenerator(request);

        // then
        assertThat(excelReportGenerator).isNotNull();
    }

    @SneakyThrows
    private File createFileInTemporaryDirectory(String fileName) {
        File file = new File(temporaryDirectory, fileName);

        Assert.isTrue(file.createNewFile(), "File has not been created for test");

        return file;
    }
}
