package net.croz.nrich.excel.request;

import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.util.Map;

@Getter
@Builder
public class CreateReportGeneratorRequest {

    private File outputFile;

    private String templatePath;

    private  Map<String, String> templateVariableMap;

    private Map<Integer, String> cellDataFormatMap;

    private int rowIndex;

}
