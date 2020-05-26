package net.croz.nrich.excel.generator;

public interface ExcelExportGenerator {

    void writeRowData(final Object ...reportDataList);

    void flushAndClose();
}
