package net.croz.nrich.excel.api.model;

/**
 * Provides an array of rows that will be written to excel. Each row is represented as an array of objects.
 */
@FunctionalInterface
public interface MultiRowDataProvider {

    /**
     * Returns an array of rows to be written to excel report. Method is called with incrementing start argument until it returns null or empty array.
     *
     * @param start index of first row
     * @param limit how many rows should be returned
     * @return an array of rows to be written to excel report
     */
    Object[][] resolveMultiRowData(int start, int limit);

}
