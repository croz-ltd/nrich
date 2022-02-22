package net.croz.nrich.search.starter.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.search")
public class NrichSearchProperties {

    /**
     * String search configuration used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} that is used for string search by {@link net.croz.nrich.search.api.repository.StringSearchExecutor}
     */
    private final StringSearchProperties stringSearch;

    /**
     * Whether default string to type converter ({@link net.croz.nrich.search.converter.DefaultStringToTypeConverter}) used for converting strings to property values when querying is enabled
     */
    private final boolean defaultConverterEnabled;

    public NrichSearchProperties(@DefaultValue StringSearchProperties stringSearch, @DefaultValue("true") boolean defaultConverterEnabled) {
        this.stringSearch = stringSearch;
        this.defaultConverterEnabled = defaultConverterEnabled;
    }

    @Getter
    public static class StringSearchProperties {

        /**
         * List of date formats used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} to convert string to date values
         */
        private final List<String> dateFormatList;

        /**
         * List of decimal formats used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} to convert string to decimal value
         */
        private final List<String> decimalNumberFormatList;

        /**
         * Regexp pattern that is used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} to match boolean true values
         */
        private final String booleanTrueRegexPattern;

        /**
         * Regexp pattern that is used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter} to match boolean false values
         */
        private final String booleanFalseRegexPattern;

        public StringSearchProperties(@DefaultValue({ "dd.MM.yyyy", "yyyy-MM-dd'T'HH:mm" }) List<String> dateFormatList, @DefaultValue({ "#0.00", "#0,00" }) List<String> decimalNumberFormatList, @DefaultValue("^(?i)\\s*(true|yes|da)\\s*$") String booleanTrueRegexPattern, @DefaultValue("^(?i)\\s*(false|no|ne)\\s*$") String booleanFalseRegexPattern) {
            this.dateFormatList = dateFormatList;
            this.decimalNumberFormatList = decimalNumberFormatList;
            this.booleanTrueRegexPattern = booleanTrueRegexPattern;
            this.booleanFalseRegexPattern = booleanFalseRegexPattern;
        }
    }
}
