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

    /**
     * Date format used to set excel cell style for date values (i.e. {@link java.time.LocalDate})
     */
    private final String dateFormat;

    /**
     * Date time format used to set excel cell style for date time values (i.e. {@link java.time.LocalDateTime})
     */
    private final String dateTimeFormat;

    /**
     * Whether dateFormat or dateTimeFormat should be used for date time values
     */
    private final boolean writeDateWithTime;

    /**
     * Integer number format used to set excel cell style for integer numbers (short, integer, long, BigInteger)
     */
    private final String integerNumberFormat;

    /**
     * Decimal number format used to set excel cell style for decimal numbers (float, double, BigDecimal)
     */
    private final String decimalNumberFormat;

    /**
     * A list of formats that overrides default formats for classes.
     */
    private final List<TypeDataFormat> typeDataFormatList;

    /**
     * Whether default converter {@link net.croz.nrich.excel.converter.DefaultCellValueConverter} should be enabled. It handles conversion of objects to values
     * accepted by excel generator implementation.
     */
    private final boolean defaultConverterEnabled;

    public NrichExcelProperties(@DefaultValue("dd.MM.yyyy.") String dateFormat, @DefaultValue("dd.MM.yyyy. HH:mm") String dateTimeFormat, @DefaultValue("false") boolean writeDateWithTime, @DefaultValue("#,##0") String integerNumberFormat, @DefaultValue("#,##0.00") String decimalNumberFormat, List<TypeDataFormat> typeDataFormatList, @DefaultValue("true") boolean defaultConverterEnabled) {
        this.dateFormat = dateFormat;
        this.dateTimeFormat = dateTimeFormat;
        this.writeDateWithTime = writeDateWithTime;
        this.integerNumberFormat = integerNumberFormat;
        this.decimalNumberFormat = decimalNumberFormat;
        this.typeDataFormatList = typeDataFormatList;
        this.defaultConverterEnabled = defaultConverterEnabled;
    }
}
