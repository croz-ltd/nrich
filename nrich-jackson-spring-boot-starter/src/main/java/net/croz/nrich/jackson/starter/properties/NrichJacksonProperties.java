package net.croz.nrich.jackson.starter.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.jackson")
public class NrichJacksonProperties {

    private final boolean convertEmptyStringsToNull;

    private final boolean serializeClassName;

    private final boolean serializeClassNameForEntityAnnotatedClasses;

    private final List<String> additionalPackageListForClassNameSerialization;

    public NrichJacksonProperties(@DefaultValue("true") final boolean convertEmptyStringsToNull, @DefaultValue("true") final boolean serializeClassName, @DefaultValue("true") final boolean serializeClassNameForEntityAnnotatedClasses, final List<String> additionalPackageListForClassNameSerialization) {
        this.convertEmptyStringsToNull = convertEmptyStringsToNull;
        this.serializeClassName = serializeClassName;
        this.serializeClassNameForEntityAnnotatedClasses = serializeClassNameForEntityAnnotatedClasses;
        this.additionalPackageListForClassNameSerialization = additionalPackageListForClassNameSerialization;
    }
}
