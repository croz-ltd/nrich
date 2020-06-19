package net.croz.nrich.excel.starter.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.excel")
public class ExcelProperties {

    private final String dateFormat;

    private final String dateTimeFormat;

    private final String integerNumberFormat;

    private final String decimalNumberFormat;

    private final boolean writeDateWithTime;

    private final boolean defaultConverterEnabled;

    public ExcelProperties(@DefaultValue("dd.MM.yyyy.") final String dateFormat, @DefaultValue("dd.MM.yyyy. HH:mm") final String dateTimeFormat, @DefaultValue("#,##0") final String integerNumberFormat, @DefaultValue("#,##0.00") final String decimalNumberFormat, @DefaultValue("false") final boolean writeDateWithTime, @DefaultValue("true") final boolean defaultConverterEnabled) {
        this.dateFormat = dateFormat;
        this.dateTimeFormat = dateTimeFormat;
        this.integerNumberFormat = integerNumberFormat;
        this.decimalNumberFormat = decimalNumberFormat;
        this.writeDateWithTime = writeDateWithTime;
        this.defaultConverterEnabled = defaultConverterEnabled;
    }
}
