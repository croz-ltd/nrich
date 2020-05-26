package net.croz.nrich.excel.request;

import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.util.Map;

@Getter
@Builder
public class CreateReportGeneratorRequest {

    private final File outputFile;

    private final String templatePath;

    private final Map<String, String> templateVariableMap;

    private final Map<Integer, String> cellDataFormatMap;

    private final int rowIndex;

}
