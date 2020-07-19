package net.croz.nrich.excel.generator;

import net.croz.nrich.excel.api.converter.CellValueConverter;
import net.croz.nrich.excel.api.model.ColumnDataFormat;
import net.croz.nrich.excel.api.model.TemplateVariable;
import net.croz.nrich.excel.api.model.TypeDataFormat;
import net.croz.nrich.excel.converter.DefaultCellValueConverter;
import net.croz.nrich.excel.util.TypeDataFormatUtil;
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
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.getCellValue;
import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.getRowCellStyleList;
import static net.croz.nrich.excel.testutil.PoiDataResolverUtil.getRowCellValueList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class PoiExcelExportGeneratorTest {

    private static final String REPORT_FILE_NAME = "report.xlsx";

    private static final int TEMPLATE_DATA_FIRST_ROW_INDEX = 3;

    private PoiExcelExportGenerator excelExportGenerator;

    @TempDir
    File temporaryDirectory;

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @BeforeEach
    void setup() {
        final CellValueConverter cellValueConverter = new DefaultCellValueConverter();
        final InputStream template = this.getClass().getResourceAsStream("/excel/template.xlsx");
        final List<TemplateVariable> templateVariableList = Collections.singletonList(new TemplateVariable("templateVariable", "resolvedValue"));
        final List<ColumnDataFormat> columnDataFormatList = Arrays.asList(new ColumnDataFormat(2, "dd-MM-yyyy"), new ColumnDataFormat(3, "dd-MM-yyyy HH:mm"));

        final List<TypeDataFormat> typeDataFormatList = TypeDataFormatUtil.resolveTypeDataFormatList("dd.MM.yyyy.", "dd.MM.yyyy. HH:mm", "#,##0", "#,##0.00", true, Collections.singletonList(new TypeDataFormat(Date.class, "dd-MM-yyyy")));

        excelExportGenerator = new PoiExcelExportGenerator(Collections.singletonList(cellValueConverter), new File(temporaryDirectory, REPORT_FILE_NAME), template, templateVariableList, typeDataFormatList, columnDataFormatList, TEMPLATE_DATA_FIRST_ROW_INDEX);
    }

    @Test
    void shouldExportDataToExcel() throws Exception {
        // given
        final Instant now = Instant.now().truncatedTo(ChronoUnit.DAYS);
        final Object[] rowData = new Object[] { 1.1, "value", new Date(now.toEpochMilli()), ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS), OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS), now, now, 1, 1.5F, (short) 1, LocalDate.now(), LocalDateTime.now().truncatedTo(ChronoUnit.DAYS), BigDecimal.valueOf(1.5), 10L, null };
        // when resolving data from cells all dates are converted to instant, all decimal numbers are converted to double and all whole numbers are converted to integer
        final Object[] expectedRowData = new Object[] { 1.1, "value", now, now, now, now, now, 1, 1.5, 1, now, now, 1.5, 10, null };

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

    @Test
    void shouldThrowExceptionWhenTryingToWriteToClosedGenerator() {
        // given
        final Object[] rowData = new Object[] { 1.0, "value", Instant.now().truncatedTo(ChronoUnit.DAYS), Instant.now().truncatedTo(ChronoUnit.DAYS) };

        excelExportGenerator.writeRowData(rowData);
        excelExportGenerator.flushAndClose();

        // when
        final Throwable thrown = catchThrowable(() -> excelExportGenerator.writeRowData(rowData));

        // then
        assertThat(thrown).isNotNull();
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldSetDefaultFormatToColumnsWithoutDefinedFormat() throws Exception {
        // given
        final Instant now = Instant.now().truncatedTo(ChronoUnit.DAYS);
        final Object[] rowData = new Object[] { 1.1, 1, now, now, (short) 1, LocalDate.now(), LocalDateTime.now().truncatedTo(ChronoUnit.DAYS), BigDecimal.valueOf(1.5), 10L, new Date() };

        // when
        excelExportGenerator.writeRowData(rowData);
        excelExportGenerator.flushAndClose();

        // and when
        final Workbook workbook = new XSSFWorkbook(new File(temporaryDirectory, REPORT_FILE_NAME));
        final Sheet sheet = workbook.getSheetAt(0);

        // then
        assertThat(getRowCellStyleList(sheet.getRow(TEMPLATE_DATA_FIRST_ROW_INDEX))).containsExactly("#,##0.00", "#,##0", "dd-MM-yyyy", "dd-MM-yyyy HH:mm", "#,##0", "dd.MM.yyyy.", "dd.MM.yyyy. HH:mm", "#,##0.00", "#,##0", "dd-MM-yyyy");
    }
}
