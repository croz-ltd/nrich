package net.croz.nrich.excel.generator;

import lombok.SneakyThrows;
import net.croz.nrich.excel.converter.CellValueConverter;
import net.croz.nrich.excel.converter.impl.DefaultCellValueConverter;
import net.croz.nrich.excel.model.CellDataFormat;
import net.croz.nrich.excel.model.TemplateVariable;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.getCellValue;
import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.getRowCellStyleList;
import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.getRowCellValueList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultExcelExportGeneratorTest {

    private static final String REPORT_FILE_NAME = "report.xlsx";

    private static final int TEMPLATE_DATA_FIRST_ROW_INDEX = 3;

    private DefaultExcelExportGenerator excelExportGenerator;

    @TempDir
    File temporaryDirectory;

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @SneakyThrows
    @BeforeEach
    void setup() {
        final CellValueConverter cellValueConverter = new DefaultCellValueConverter("dd.MM.yyyy.", "dd.MM.yyyy. HH:mm", "#,##0", "#,##0.00", true);
        final InputStream template = this.getClass().getResourceAsStream("/excel/template.xlsx");
        final List<TemplateVariable> templateVariableList = Collections.singletonList(new TemplateVariable("templateVariable", "resolvedValue"));
        final List<CellDataFormat> cellDataFormatList = Arrays.asList(new CellDataFormat(2, "dd-MM-yyyy"), new CellDataFormat(3, "dd-MM-yyyy HH:mm"));

        excelExportGenerator = new DefaultExcelExportGenerator(Collections.singletonList(cellValueConverter), new File(temporaryDirectory, REPORT_FILE_NAME), template, templateVariableList, cellDataFormatList, TEMPLATE_DATA_FIRST_ROW_INDEX);
    }

    @SneakyThrows
    @Test
    void shouldExportDataToExcel() {
        // given
        final Instant now = Instant.now().truncatedTo(ChronoUnit.DAYS);
        final Object[] rowData = new Object[] { 1.1, "value", now, now, 1, 1.5F, (short) 1, LocalDate.now(), LocalDateTime.now().truncatedTo(ChronoUnit.DAYS), BigDecimal.valueOf(1.5), 10L, null };
        // when resolving data from cells all dates are converted to instant, all decimal numbers are converted to double and all whole numbers are converted to integer
        final Object[] expectedRowData = new Object[] { 1.1, "value", now, now, 1, 1.5, 1, now, now, 1.5, 10, null };

        // when
        excelExportGenerator.writeRowData(rowData);
        excelExportGenerator.flushAndClose();

        // and when
        final Workbook workbook = new XSSFWorkbook(new File(temporaryDirectory, REPORT_FILE_NAME));
        final Sheet sheet = workbook.getSheetAt(0);

        // then
        assertThat(sheet).isNotNull();

        assertThat(sheet.getRow(0).getCell(0)).isNotNull();
        assertThat(getCellValue(sheet.getRow(0).getCell(0))).isEqualTo("resolvedValue");
        assertThat(getRowCellValueList(sheet.getRow(TEMPLATE_DATA_FIRST_ROW_INDEX))).containsExactly(expectedRowData);
    }

    @SneakyThrows
    @Test
    void shouldThrowExceptionWhenTryingToWriteToClosedGenerator() {
        // given
        final Object[] rowData = new Object[] { 1.0, "value", Instant.now().truncatedTo(ChronoUnit.DAYS), Instant.now().truncatedTo(ChronoUnit.DAYS) };

        // when
        excelExportGenerator.writeRowData(rowData);
        excelExportGenerator.flushAndClose();

        // then
        assertThrows(IllegalArgumentException.class, () -> excelExportGenerator.writeRowData(rowData));
    }

    @SneakyThrows
    @Test
    void shouldSetDefaultFormatToColumnsWithoutDefinedFormat() {
        // given
        final Instant now = Instant.now().truncatedTo(ChronoUnit.DAYS);
        final Object[] rowData = new Object[] { 1.1, 1, now, now, (short) 1, LocalDate.now(), LocalDateTime.now().truncatedTo(ChronoUnit.DAYS), BigDecimal.valueOf(1.5), 10L };

        // when
        excelExportGenerator.writeRowData(rowData);
        excelExportGenerator.flushAndClose();

        // and when
        final Workbook workbook = new XSSFWorkbook(new File(temporaryDirectory, REPORT_FILE_NAME));
        final Sheet sheet = workbook.getSheetAt(0);

        // then
        assertThat(getRowCellStyleList(sheet.getRow(TEMPLATE_DATA_FIRST_ROW_INDEX))).containsExactly("#,##0.00", "#,##0", "dd-MM-yyyy", "dd-MM-yyyy HH:mm", "#,##0", "dd.MM.yyyy.", "dd.MM.yyyy. HH:mm", "#,##0.00", "#,##0");
    }
}
