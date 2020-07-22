package net.croz.nrich.excel.api.request;

import lombok.Builder;
import lombok.Getter;
import net.croz.nrich.excel.api.model.ColumnDataFormat;
import net.croz.nrich.excel.api.model.MultiRowDataProvider;
import net.croz.nrich.excel.api.model.TemplateVariable;

import java.io.File;
import java.util.List;

@Getter
@Builder
public class CreateExcelReportRequest {

    /**
     * File where report will be written to.
     */
    private final File outputFile;

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
     * Row index from which data should be written to report (if for example template holds column headers in first couple of rows).
     */
    private final int firstRowIndex;

    /**
     *   Batch size used for data fetch from {@link MultiRowDataProvider}.
     */
    private final int batchSize;

    /**
     * Interface that is used for fetching data, it should return multiple row data.
     */
    private final MultiRowDataProvider multiRowDataProvider;

}
