package net.croz.nrich.excel.api.request;

import lombok.Builder;
import lombok.Getter;
import net.croz.nrich.excel.api.model.ColumnDataFormat;
import net.croz.nrich.excel.api.model.MultiRowDataProvider;
import net.croz.nrich.excel.api.model.TemplateVariable;

import java.io.OutputStream;
import java.util.List;

@Getter
@Builder
public class CreateExcelReportRequest {

    /**
     * OutputStream where report will be written to (keep in mind closing of it is users responsibility).
     */
    private final OutputStream outputStream;

    /**
     * Path to template (template is resolved from this path using Springs ResourceLoader)
     */
    private final String templatePath;

    /**
     * List of {@link TemplateVariable} instances that will be used to replace variables defined in the template.
     */
    private final List<TemplateVariable> templateVariableList;

    /**
     * List of {@link ColumnDataFormat} instances that allow for overriding of data format for specific columns.
     */
    private final List<ColumnDataFormat> columnDataFormatList;

    /**
     * Row indexes from which data should be written to report (if for example template holds column headers in first couple of rows).
     */
    private final int firstRowIndex;

    /**
     * Batch size used for data fetch from {@link MultiRowDataProvider}.
     */
    private final int batchSize;

    /**
     * Interface that is used for fetching data, it should return multiple row data.
     */
    private final MultiRowDataProvider multiRowDataProvider;

    /**
     * Creates {@link CreateExcelReportRequest} Builder instance from flat data.
     *
     * @param data Flat data to be written
     * @return A {@link CreateExcelReportRequest} builder instance
     */
    public static CreateExcelReportRequest.CreateExcelReportRequestBuilder fromFlatData(Object[][] data) {
        return CreateExcelReportRequest.builder().multiRowDataProvider((start, limit) -> start == 0 ? data : null);
    }

    /**
     * Creates {@link CreateExcelReportRequest} Builder instance from {@link MultiRowDataProvider} instance.
     *
     * @param multiRowDataProvider Row provider for data to be written
     * @return A {@link CreateExcelReportRequest} builder instance
     */
    public static CreateExcelReportRequest.CreateExcelReportRequestBuilder fromRowDataProvider(MultiRowDataProvider multiRowDataProvider) {
        return CreateExcelReportRequest.builder().multiRowDataProvider(multiRowDataProvider);
    }

    /**
     * CreateExcelReportRequest builder (explicit to avoid errors while publishing javadoc).
     */
    public static class CreateExcelReportRequestBuilder { // NOSONAR
    }

    private static CreateExcelReportRequest.CreateExcelReportRequestBuilder builder() {
        return new CreateExcelReportRequest.CreateExcelReportRequestBuilder();
    }
}
