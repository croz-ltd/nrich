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

    private final File outputFile;

    private final String templatePath;

    private final List<TemplateVariable> templateVariableList;

    private final List<ColumnDataFormat> columnDataFormatList;

    private final int firstRowIndex;

    private final int batchSize;

    private final MultiRowDataProvider multiRowDataProvider;

}
