package net.croz.nrich.excel.generator;

import lombok.SneakyThrows;
import net.croz.nrich.excel.api.converter.CellValueConverter;
import net.croz.nrich.excel.api.generator.ExcelReportGenerator;
import net.croz.nrich.excel.api.model.ColumnDataFormat;
import net.croz.nrich.excel.api.model.TemplateVariable;
import net.croz.nrich.excel.api.model.TypeDataFormat;
import net.croz.nrich.excel.model.PoiCellHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PoiExcelReportGenerator implements ExcelReportGenerator {

    private static final Pattern TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\$\\{(.*?)}");

    private final List<CellValueConverter> cellValueConverterList;

    private final OutputStream outputStream;

    private final SXSSFWorkbook workbook;

    private final Sheet sheet;

    private final CreationHelper creationHelper;

    private final Map<Integer, CellStyle> cellStyleMap;

    private final Map<Class<?>, CellStyle> defaultStyleMap;

    private int currentRowNumber;

    private boolean templateOpen = true;

    public PoiExcelReportGenerator(List<CellValueConverter> cellValueConverterList, OutputStream outputStream, InputStream template, List<TemplateVariable> templateVariableList,
                                   List<TypeDataFormat> typeDataFormatList, List<ColumnDataFormat> columnDataFormatList, int startIndex) {
        this.cellValueConverterList = cellValueConverterList;
        this.outputStream = outputStream;
        this.workbook = initializeWorkBookWithTemplate(template, templateVariableList);
        this.sheet = workbook.getSheetAt(0);
        this.creationHelper = workbook.getCreationHelper();
        this.cellStyleMap = createStyleMap(columnDataFormatList);
        this.defaultStyleMap = createDefaultStyleMap(typeDataFormatList);
        this.currentRowNumber = startIndex;
    }

    @Override
    public void writeRowData(Object... reportDataList) {
        Assert.isTrue(templateOpen, "Template has benn closed and cannot be written anymore");

        Row row = sheet.createRow(currentRowNumber++);

        IntStream.range(0, reportDataList.length).forEach(index -> {
            Object value = reportDataList[index];
            Cell cell = row.createCell(index);
            CellStyle defaultStyle = Optional.ofNullable(value).map(Object::getClass).map(defaultStyleMap::get).orElse(null);
            CellStyle cellStyle = Optional.ofNullable(cellStyleMap.get(index)).orElse(defaultStyle);

            setCellValue(cell, value, cellStyle);
        });
    }

    @SneakyThrows
    @Override
    public void flush() {
        workbook.write(outputStream);
        this.templateOpen = false;
    }

    private void processTemplateVariableMap(Sheet sheet, List<TemplateVariable> templateVariableList) {
        if (templateVariableList == null) {
            return;
        }

        sheet.forEach(row -> row.forEach(cell -> {
            Matcher matcher = TEMPLATE_VARIABLE_PATTERN.matcher(cell.getStringCellValue());

            if (matcher.find()) {
                String matchedExpression = matcher.group(1);
                String variableValue = templateVariableList.stream()
                    .filter(variable -> matchedExpression.equals(variable.getName()))
                    .map(TemplateVariable::getValue)
                    .findFirst()
                    .orElse("");

                String updatedValue = matcher.replaceFirst(variableValue);

                setCellValue(cell, updatedValue, cell.getCellStyle());
            }
        }));
    }

    private void setCellValue(Cell cell, Object value, CellStyle style) {
        if (value == null) {
            return;
        }

        PoiCellHolder cellHolder = new PoiCellHolder(cell);

        CellValueConverter converter = cellValueConverterList.stream()
            .filter(cellValueConverter -> cellValueConverter.supports(cellHolder, value))
            .findFirst()
            .orElse(null);

        if (converter == null) {
            cell.setCellValue(value.toString());
        }
        else {
            converter.setCellValue(cellHolder, value);
        }

        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private Map<Integer, CellStyle> createStyleMap(List<ColumnDataFormat> columnDataFormatList) {
        if (columnDataFormatList == null) {
            return new HashMap<>();
        }

        return columnDataFormatList.stream()
            .collect(Collectors.toMap(ColumnDataFormat::getColumnIndex, entry -> createCellStyle(entry.getDataFormat())));
    }

    private CellStyle createCellStyle(String dataFormat) {
        CellStyle style = workbook.createCellStyle();

        if (dataFormat != null) {
            style.setDataFormat(creationHelper.createDataFormat().getFormat(dataFormat));
        }

        return style;
    }

    @SneakyThrows
    private SXSSFWorkbook initializeWorkBookWithTemplate(InputStream template, List<TemplateVariable> templateVariableList) {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(template);

        processTemplateVariableMap(xssfWorkbook.getSheetAt(0), templateVariableList);

        return new SXSSFWorkbook(xssfWorkbook);
    }

    private Map<Class<?>, CellStyle> createDefaultStyleMap(List<TypeDataFormat> typeDataFormatList) {
        return typeDataFormatList.stream()
            .filter(typeDataFormat -> typeDataFormat.getDataFormat() != null)
            .collect(Collectors.toMap(TypeDataFormat::getType, value -> createCellStyle(value.getDataFormat())));
    }
}
