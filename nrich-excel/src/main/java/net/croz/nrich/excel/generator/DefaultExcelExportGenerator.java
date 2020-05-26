package net.croz.nrich.excel.generator;

import hr.apis.m19.jlprs.infrastructure.excel.converter.CellValueConverter;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.internal.util.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultExcelExportGenerator implements ExcelExportGenerator {

    private static final Pattern TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\$\\{(.*?)}");

    private final File outputFile;

    private final SXSSFWorkbook workbook;

    private final Sheet sheet;

    private final CreationHelper creationHelper;

    private final Map<Integer, CellStyle> cellStyleMap;

    private final Map<Class<?>, CellStyle> defaultStyleMap;

    private int currentRowNumber;

    private boolean templateOpen = true;

    public DefaultExcelExportGenerator(final File outputFile, final InputStream template, final Map<String, String> templateVariableMap, final Map<Integer, String> dataFormatMap, final int startIndex) {
        this.outputFile = outputFile;
        this.workbook = initializeWorkBookWithTemplate(template, templateVariableMap);
        this.sheet = workbook.getSheetAt(0);
        this.creationHelper = workbook.getCreationHelper();
        this.cellStyleMap = createStyleMap(dataFormatMap);
        this.defaultStyleMap = createDefaultStyleMap();

        this.currentRowNumber = startIndex;
    }

    @Override
    public void writeRowData(final Object...reportDataList) {
        Assert.isTrue(templateOpen, "Template has benn closed and cannot be written anymore");

        final Row row = sheet.createRow(currentRowNumber++);

        IntStream.range(0, reportDataList.length).forEach(index -> {
            final Object value = reportDataList[index];
            final Cell cell = row.createCell(index);
            final CellStyle defaultStyle = Optional.ofNullable(value).map(Object::getClass).map(defaultStyleMap::get).orElse(null);

            setCellValue(cell, value, Optional.ofNullable(cellStyleMap.get(index)).orElse(defaultStyle));
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

    private void processTemplateVariableMap(final Sheet sheet, final Map<String, String> templateVariableMap) {
        if (templateVariableMap == null) {
            return;
        }

        sheet.forEach(row -> row.forEach(cell -> {
            final Matcher matcher = TEMPLATE_VARIABLE_PATTERN.matcher(cell.getStringCellValue());

            if (matcher.find()) {
                final String matchedExpression = matcher.group(1);
                final String variableValue = templateVariableMap.getOrDefault(matchedExpression, "");

                final String updatedValue = matcher.replaceFirst(variableValue == null ? "" : variableValue);

                setCellValue(cell, updatedValue, cell.getCellStyle());
            }
        }));
    }

    private void setCellValue(final Cell cell, final Object value, final CellStyle style) {
        if (value == null) {
            return;
        }

        final CellValueConverter converter =  CellValueConverter.forType(value.getClass());
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

    private Map<Integer, CellStyle> createStyleMap(final Map<Integer, String> dateFormatMap) {
        if (dateFormatMap == null) {
            return new HashMap<>();
        }

        return dateFormatMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> createCellStyle(entry.getValue())));
    }

    private CellStyle createCellStyle(final String dataFormat) {
        final CellStyle style = workbook.createCellStyle();

        if (dataFormat != null) {
            style.setDataFormat(creationHelper.createDataFormat().getFormat(dataFormat));
        }

        return style;
    }

    @SneakyThrows
    private SXSSFWorkbook initializeWorkBookWithTemplate(final InputStream template, final Map<String, String> templateVariableMap) {
        final XSSFWorkbook xssfWorkbook = new XSSFWorkbook(template);

        processTemplateVariableMap(xssfWorkbook.getSheetAt(0), templateVariableMap);

        return new SXSSFWorkbook(xssfWorkbook);
    }

    private Map<Class<?>, CellStyle> createDefaultStyleMap() {
        return Arrays.stream(CellValueConverter.values()).collect(Collectors.toMap(CellValueConverter::getType, value -> createCellStyle(value.getDataFormat())));
    }
}
