package net.croz.nrich.excel.model;

@FunctionalInterface
public interface RowDataProvider {

    Object[][] resolveRowData(int start, int limit);

}
