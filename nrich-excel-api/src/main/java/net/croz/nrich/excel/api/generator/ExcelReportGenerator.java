package net.croz.nrich.excel.api.generator;

/**
 * Writes data to excel report.
 */
public interface ExcelReportGenerator {

    /**
     * Write a single row to excel report.
     *
     * @param reportDataList data to write as a row in excel report.
     */
    void writeRowData(Object... reportDataList);

    /**
     * Flushes data to report and closes it. No further writing is possible after this call.
     */
    void flushAndClose();

}
