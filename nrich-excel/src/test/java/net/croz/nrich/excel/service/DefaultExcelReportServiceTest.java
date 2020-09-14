package net.croz.nrich.excel.service;

import lombok.SneakyThrows;
import net.croz.nrich.excel.ExcelTestConfiguration;
import net.croz.nrich.excel.api.model.MultiRowDataProvider;
import net.croz.nrich.excel.api.request.CreateExcelReportRequest;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.Assert;

import java.io.File;

import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.getRowCellValueList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringJUnitConfig(ExcelTestConfiguration.class)
class DefaultExcelReportServiceTest {

    private static final int TEMPLATE_DATA_FIRST_ROW_INDEX = 3;

    @Autowired
    private DefaultExcelReportService excelReportService;

    @TempDir
    File temporaryDirectory;

    @Test
    void shouldCreateExcelReport() throws Exception {
        // given
        final File file = createFileInTemporaryDirectory();
        final Object[][] rowData = new Object[][] { { 1.1, "value" } };
        final MultiRowDataProvider multiRowDataProvider = (start, limit) -> start == 0 ? rowData : null;

        final CreateExcelReportRequest request = CreateExcelReportRequest.builder().multiRowDataProvider(multiRowDataProvider).batchSize(10).outputFile(file).templatePath("classpath:excel/template.xlsx").firstRowIndex(TEMPLATE_DATA_FIRST_ROW_INDEX).build();

        // when
        final File result = excelReportService.createExcelReport(request);

        // then
        final Workbook workbook = new XSSFWorkbook(result);
        final Sheet sheet = workbook.getSheetAt(0);

        // then
        assertThat(sheet).isNotNull();

        assertThat(sheet.getRow(0).getCell(0)).isNotNull();
        assertThat(getRowCellValueList(sheet.getRow(TEMPLATE_DATA_FIRST_ROW_INDEX))).containsExactly(rowData[0]);
    }

    @Test
    void shouldThrowExceptionOnInvalidBatchSize() {
        // given
        final File file = createFileInTemporaryDirectory();
        final Object[][] rowData = new Object[][] { { 1.1, "value" } };
        final MultiRowDataProvider multiRowDataProvider = (start, limit) -> start == 0 ? rowData : null;

        final CreateExcelReportRequest request = CreateExcelReportRequest.builder().multiRowDataProvider(multiRowDataProvider).batchSize(-1).outputFile(file).templatePath("classpath:excel/template.xlsx").firstRowIndex(TEMPLATE_DATA_FIRST_ROW_INDEX).build();

        // when
        final Throwable thrown = catchThrowable(() -> excelReportService.createExcelReport(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionOnMissingRowDataProvider() {
        // given
        final File file = createFileInTemporaryDirectory();

        final CreateExcelReportRequest request = CreateExcelReportRequest.builder().batchSize(10).outputFile(file).templatePath("classpath:excel/template.xlsx").firstRowIndex(TEMPLATE_DATA_FIRST_ROW_INDEX).build();

        // when
        final Throwable thrown = catchThrowable(() -> excelReportService.createExcelReport(request));

        // then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @SneakyThrows
    private File createFileInTemporaryDirectory() {
        final File file = new File(temporaryDirectory, "export.xlxs");

        Assert.isTrue(file.createNewFile(), "File has not been created for test");

        return file;
    }

}
