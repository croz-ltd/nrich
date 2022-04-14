package net.croz.nrich.registry.starter.properties;

import lombok.Getter;
import net.croz.nrich.registry.api.core.model.RegistryConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.registry")
public class NrichRegistryProperties {

    /**
     * List of property names that should always be marked as readonly
     */
    private final List<String> defaultReadOnlyPropertyList;

    /**
     * Registry search configuration used by {@link net.croz.nrich.search.converter.DefaultStringToTypeConverter}
     */
    private final RegistrySearchProperties registrySearch;

    /**
     * Whether default string to type converter ({@link net.croz.nrich.search.converter.DefaultStringToTypeConverter}) used for converting strings to property values when querying registry is enabled
     */
    private final boolean defaultConverterEnabled;

    /**
     * Registry configuration used for defining entities and groups which will be managed.
     */
    private final RegistryConfiguration registryConfiguration;

    public NrichRegistryProperties(List<String> defaultReadOnlyPropertyList, @DefaultValue RegistrySearchProperties registrySearch, @DefaultValue("true") boolean defaultConverterEnabled,
                                   RegistryConfiguration registryConfiguration) {
        this.defaultReadOnlyPropertyList = defaultReadOnlyPropertyList;
        this.registrySearch = registrySearch;
        this.defaultConverterEnabled = defaultConverterEnabled;
        this.registryConfiguration = registryConfiguration;
    }

    @Getter
    public static class RegistrySearchProperties {

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

        public RegistrySearchProperties(@DefaultValue({ "dd.MM.yyyy.", "dd.MM.yyyy.'T'HH:mm", "dd.MM.yyyy.'T'HH:mm'Z'" }) List<String> dateFormatList, @DefaultValue({ "#0.00", "#0,00" }) List<String> decimalNumberFormatList,
                                        @DefaultValue("^(?i)\\s*(true|yes|da)\\s*$") String booleanTrueRegexPattern, @DefaultValue("^(?i)\\s*(false|no|ne)\\s*$") String booleanFalseRegexPattern) {
            this.dateFormatList = dateFormatList;
            this.decimalNumberFormatList = decimalNumberFormatList;
            this.booleanTrueRegexPattern = booleanTrueRegexPattern;
            this.booleanFalseRegexPattern = booleanFalseRegexPattern;
        }
    }
}
