package net.croz.nrich.excel.testutil;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

public final class PoiDataResolverUtil {

    private PoiDataResolverUtil() {
    }

    public static Object getCellValue(final Cell cell) {
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

    public static List<Object> getRowCellValueList(final Row row) {
        final List<Object> resultList = new ArrayList<>();

        row.forEach(cell -> resultList.add(getCellValue(cell)));

        return resultList;
    }

    private static Object asIntegerIfApplicable(final double value) {
        final int intValue = Double.valueOf(value).intValue();

        if (intValue == value) {
            return intValue;
        }

        return value;
    }

    public static List<String> getRowCellStyleList(final Row row) {
        final List<String> resultList = new ArrayList<>();

        row.forEach(cell -> resultList.add(cell.getCellStyle().getDataFormatString()));

        return resultList;
    }

}
