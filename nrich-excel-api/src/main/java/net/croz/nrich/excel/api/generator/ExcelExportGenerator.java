package net.croz.nrich.excel.api.generator;

public interface ExcelExportGenerator {

    void writeRowData(final Object ...reportDataList);

    void flushAndClose();
}
