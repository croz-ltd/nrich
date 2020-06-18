package net.croz.nrich.excel.generator;

import lombok.SneakyThrows;
import net.croz.nrich.excel.ExcelTestConfiguration;
import net.croz.nrich.excel.api.generator.ExcelExportGenerator;
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
public class PoiExcelExportGeneratorFactoryTest {

    @Autowired
    private PoiExcelExportGeneratorFactory excelExportGeneratorFactory;

    @TempDir
    File temporaryDirectory;

    @Test
    void shouldThrowExceptionWhenOutputFileDoesntExist() {
        // given
        final CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().build();

        // when
        final Throwable thrown = catchThrowable(() -> excelExportGeneratorFactory.createReportGenerator(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenTemplateFileDoesntExist() {
        // given
        final CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().outputFile(new File(temporaryDirectory, "output.xlsx")).build();

        // when
        final Throwable thrown = catchThrowable(() -> excelExportGeneratorFactory.createReportGenerator(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenRowIndexIsNegative() {
        // given
        final File file = createFileInTemporaryDirectory("missing-row.xlxs");

        final CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().outputFile(file).templatePath("classpath:excel/template.xlsx").firstRowIndex(-1).build();

        // when
        final Throwable thrown = catchThrowable(() -> excelExportGeneratorFactory.createReportGenerator(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldReturnReportGeneratorWhenDataIsValid() {
        // given
        final File file = createFileInTemporaryDirectory("valid.xlxs");

        final CreateReportGeneratorRequest request = CreateReportGeneratorRequest.builder().outputFile(file).templatePath("classpath:excel/template.xlsx").firstRowIndex(1).build();

        // when
        final ExcelExportGenerator excelExportGenerator = excelExportGeneratorFactory.createReportGenerator(request);

        // then
        assertThat(excelExportGenerator).isNotNull();
    }

    @SneakyThrows
    private File createFileInTemporaryDirectory(final String fileName) {
        final File file = new File(temporaryDirectory, fileName);

        Assert.isTrue(file.createNewFile(), "File has not been created for test");

        return file;
    }
}
