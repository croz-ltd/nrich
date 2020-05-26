package net.croz.nrich.excel.model;

@FunctionalInterface
public interface MultiRowDataProvider {

    Object[][] resolveMultiRowData(int start, int limit);

}
