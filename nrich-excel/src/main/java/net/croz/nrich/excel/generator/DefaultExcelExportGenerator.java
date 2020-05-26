package net.croz.nrich.excel.generator;

import lombok.SneakyThrows;
import net.croz.nrich.excel.converter.CellValueConverter;
import net.croz.nrich.excel.model.CellDataFormat;
import net.croz.nrich.excel.model.TemplateVariable;
import net.croz.nrich.excel.model.TypeDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultExcelExportGenerator implements ExcelExportGenerator {

    private static final Pattern TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\$\\{(.*?)}");

    private final List<CellValueConverter> cellValueConverterList;

    private final File outputFile;

    private final SXSSFWorkbook workbook;

    private final Sheet sheet;

    private final CreationHelper creationHelper;

    private final Map<Integer, CellStyle> cellStyleMap;

    private final Map<Class<?>, CellStyle> defaultStyleMap;

    private int currentRowNumber;

    private boolean templateOpen = true;

    public DefaultExcelExportGenerator(final List<CellValueConverter> cellValueConverterList, final File outputFile, final InputStream template, final List<TemplateVariable> templateVariableList, final List<CellDataFormat> cellDataFormatList, final int startIndex) {
        this.cellValueConverterList = cellValueConverterList;
        this.outputFile = outputFile;
        this.workbook = initializeWorkBookWithTemplate(template, templateVariableList);
        this.sheet = workbook.getSheetAt(0);
        this.creationHelper = workbook.getCreationHelper();
        this.cellStyleMap = createStyleMap(cellDataFormatList);
        this.defaultStyleMap = createDefaultStyleMap();

        this.currentRowNumber = startIndex;
    }

    @Override
    public void writeRowData(final Object... reportDataList) {
        Assert.isTrue(templateOpen, "Template has benn closed and cannot be written anymore");

        final Row row = sheet.createRow(currentRowNumber++);

        IntStream.range(0, reportDataList.length).forEach(index -> {
            final Object value = reportDataList[index];
            final Cell cell = row.createCell(index);
            final CellStyle defaultStyle = Optional.ofNullable(value).map(Object::getClass).map(defaultStyleMap::get).orElse(null);
            final CellStyle cellStyle = Optional.ofNullable(cellStyleMap.get(index)).orElse(defaultStyle);

            setCellValue(cell, value, cellStyle);
        });
    }

    @SneakyThrows
    @Override
    public void flushAndClose() {
        try (final FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            workbook.write(outputStream);
        }
        this.templateOpen = false;
    }

    private void processTemplateVariableMap(final Sheet sheet, final List<TemplateVariable> templateVariableList) {
        if (templateVariableList == null) {
            return;
        }

        sheet.forEach(row -> row.forEach(cell -> {
            final Matcher matcher = TEMPLATE_VARIABLE_PATTERN.matcher(cell.getStringCellValue());

            if (matcher.find()) {
                final String matchedExpression = matcher.group(1);
                final String variableValue = templateVariableList.stream()
                        .filter(variable -> matchedExpression.equals(variable.getName()))
                        .map(TemplateVariable::getValue)
                        .findFirst()
                        .orElse("");

                final String updatedValue = matcher.replaceFirst(variableValue);

                setCellValue(cell, updatedValue, cell.getCellStyle());
            }
        }));
    }

    private void setCellValue(final Cell cell, final Object value, final CellStyle style) {
        if (value == null) {
            return;
        }

        final CellValueConverter converter = cellValueConverterList.stream()
                .filter(cellValueConverter -> cellValueConverter.supports(cell, value))
                .findFirst()
                .orElse(null);

        if (converter == null) {
            cell.setCellValue(value.toString());
        }
        else {
            converter.setCellValue(cell, value);
        }

        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private Map<Integer, CellStyle> createStyleMap(final List<CellDataFormat> cellDataFormatList) {
        if (cellDataFormatList == null) {
            return new HashMap<>();
        }

        return cellDataFormatList.stream()
                .collect(Collectors.toMap(CellDataFormat::getCellIndex, entry -> createCellStyle(entry.getDataFormat())));
    }

    private CellStyle createCellStyle(final String dataFormat) {
        final CellStyle style = workbook.createCellStyle();

        if (dataFormat != null) {
            style.setDataFormat(creationHelper.createDataFormat().getFormat(dataFormat));
        }

        return style;
    }

    @SneakyThrows
    private SXSSFWorkbook initializeWorkBookWithTemplate(final InputStream template, final List<TemplateVariable> templateVariableList) {
        final XSSFWorkbook xssfWorkbook = new XSSFWorkbook(template);

        processTemplateVariableMap(xssfWorkbook.getSheetAt(0), templateVariableList);

        return new SXSSFWorkbook(xssfWorkbook);
    }

    private Map<Class<?>, CellStyle> createDefaultStyleMap() {
        return cellValueConverterList.stream()
                .map(CellValueConverter::typeDataFormatList)
                .flatMap(List::stream)
                .filter(typeDataFormat -> typeDataFormat.getDataFormat() != null)
                .collect(Collectors.toMap(TypeDataFormat::getType, value -> createCellStyle(value.getDataFormat())));
    }
}
