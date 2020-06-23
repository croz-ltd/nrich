package net.croz.nrich.webmvc.starter.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.webmvc")
public class WebMvcProperties {

    private final boolean exceptionAuxiliaryDataResolvingEnabled;

    private final boolean convertEmptyStringsToNull;

    private final boolean ignoreTransientFields;

    private final List<String> exceptionToUnwrapList;

    private final List<String> exceptionAuxiliaryDataToIncludeInNotification;

    public WebMvcProperties(@DefaultValue("true") final boolean exceptionAuxiliaryDataResolvingEnabled, @DefaultValue("true") final boolean convertEmptyStringsToNull, @DefaultValue("true") final boolean ignoreTransientFields, @DefaultValue("java.util.concurrent.ExecutionException") final List<String> exceptionToUnwrapList, @DefaultValue("uuid") final List<String> exceptionAuxiliaryDataToIncludeInNotification) {
        this.exceptionAuxiliaryDataResolvingEnabled = exceptionAuxiliaryDataResolvingEnabled;
        this.convertEmptyStringsToNull = convertEmptyStringsToNull;
        this.ignoreTransientFields = ignoreTransientFields;
        this.exceptionToUnwrapList = exceptionToUnwrapList;
        this.exceptionAuxiliaryDataToIncludeInNotification = exceptionAuxiliaryDataToIncludeInNotification;
    }
}
