package net.croz.nrich.excel.starter.properties;

import lombok.Getter;
import net.croz.nrich.excel.api.model.TypeDataFormat;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.excel")
public class NrichExcelProperties {

    private final String dateFormat;

    private final String dateTimeFormat;

    private final String integerNumberFormat;

    private final String decimalNumberFormat;

    private final boolean writeDateWithTime;

    private final List<TypeDataFormat> typeDataFormatList;

    private final boolean defaultConverterEnabled;

    public NrichExcelProperties(@DefaultValue("dd.MM.yyyy.") final String dateFormat, @DefaultValue("dd.MM.yyyy. HH:mm") final String dateTimeFormat, @DefaultValue("#,##0") final String integerNumberFormat, @DefaultValue("#,##0.00") final String decimalNumberFormat, @DefaultValue("false") final boolean writeDateWithTime, final List<TypeDataFormat> typeDataFormatList, @DefaultValue("true") final boolean defaultConverterEnabled) {
        this.dateFormat = dateFormat;
        this.dateTimeFormat = dateTimeFormat;
        this.integerNumberFormat = integerNumberFormat;
        this.decimalNumberFormat = decimalNumberFormat;
        this.writeDateWithTime = writeDateWithTime;
        this.typeDataFormatList = typeDataFormatList;
        this.defaultConverterEnabled = defaultConverterEnabled;
    }
}
