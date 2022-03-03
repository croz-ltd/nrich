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

    /**
     * Whether empty strings should be converted to null values
     */
    private final boolean convertEmptyStringsToNull;

    /**
     * Whether class name should be serialized
     */
    private final boolean serializeClassName;

    /**
     * Whether class name should be serialized for classes annotated with JPA Entity annotation
     */
    private final boolean serializeClassNameForEntityAnnotatedClasses;

    /**
     * Package list for which class name should be also be serialized
     */
    private final List<String> additionalPackageListForClassNameSerialization;

    public NrichJacksonProperties(@DefaultValue("true") boolean convertEmptyStringsToNull, @DefaultValue("true") boolean serializeClassName,
                                  @DefaultValue("true") boolean serializeClassNameForEntityAnnotatedClasses, List<String> additionalPackageListForClassNameSerialization) {
        this.convertEmptyStringsToNull = convertEmptyStringsToNull;
        this.serializeClassName = serializeClassName;
        this.serializeClassNameForEntityAnnotatedClasses = serializeClassNameForEntityAnnotatedClasses;
        this.additionalPackageListForClassNameSerialization = additionalPackageListForClassNameSerialization;
    }
}
