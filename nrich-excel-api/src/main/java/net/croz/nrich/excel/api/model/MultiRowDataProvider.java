package net.croz.nrich.excel.api.model;

@FunctionalInterface
public interface MultiRowDataProvider {

    Object[][] resolveMultiRowData(int start, int limit);

}
