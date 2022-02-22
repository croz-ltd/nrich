package net.croz.nrich.excel.testutil;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class PoiDataResolverUtil {

    private PoiDataResolverUtil() {
    }

    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue().toInstant() : asIntegerIfApplicable(cell.getNumericCellValue());
            case BOOLEAN:
                return cell.getBooleanCellValue();
            default:
                return null;
        }
    }

    public static List<Object> getRowCellValueList(Row row) {
        List<Object> resultList = new ArrayList<>();

        row.forEach(cell -> resultList.add(getCellValue(cell)));

        return resultList;
    }

    private static Object asIntegerIfApplicable(double value) {
        int intValue = Double.valueOf(value).intValue();

        if (intValue == value) {
            return intValue;
        }

        return value;
    }

    public static List<String> getRowCellStyleList(Row row) {
        List<String> resultList = new ArrayList<>();

        row.forEach(cell -> resultList.add(cell.getCellStyle().getDataFormatString()));

        return resultList;
    }

    @SneakyThrows
    public static Sheet createWorkbookAndResolveSheet(File file) {
        // it is necessary to close the workbook, i.e. unlock the file, in order to avoid an IOException (on some OSs)
        // in case the file is inside a @TempDir which gets automatically deleted afterwards
        try (Workbook workbook = new XSSFWorkbook(file)) {
            return workbook.getSheetAt(0);
        }
    }

}
