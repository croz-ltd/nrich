package net.croz.nrich.webmvc.starter.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.webmvc")
public class NrichWebMvcProperties {

    /**
     * Whether {@link net.croz.nrich.webmvc.advice.NotificationErrorHandlingRestControllerAdvice} controller advice is enabled
     */
    private final boolean controllerAdviceEnabled;

    /**
     * Whether default {@link net.croz.nrich.webmvc.api.service.ExceptionAuxiliaryDataResolverService} is enabled
     */
    private final boolean exceptionAuxiliaryDataResolvingEnabled;

    /**
     * Whether empty strings should be converted to null when binding requests
     */
    private final boolean convertEmptyStringsToNull;

    /**
     * Whether transient fields should be ignored when binding requests
     */
    private final boolean ignoreTransientFields;

    /**
     * List of exceptions that will be unwrapping their cause
     */
    private final List<String> exceptionToUnwrapList;

    /**
     * List of exception auxiliary data to be included in notification sent to client
     */
    private final List<String> exceptionAuxiliaryDataToIncludeInNotification;

    /**
     * Optional property. Default locale
     */
    private final String defaultLocale;

    /**
     * Optional property. List of locales users can set
     */
    private final List<String> allowedLocaleList;

    public NrichWebMvcProperties(@DefaultValue("true") boolean controllerAdviceEnabled, @DefaultValue("true") boolean exceptionAuxiliaryDataResolvingEnabled, @DefaultValue("true") boolean convertEmptyStringsToNull, @DefaultValue("true") boolean ignoreTransientFields, @DefaultValue("java.util.concurrent.ExecutionException") List<String> exceptionToUnwrapList, @DefaultValue("uuid") List<String> exceptionAuxiliaryDataToIncludeInNotification, String defaultLocale, List<String> allowedLocaleList) {
        this.controllerAdviceEnabled = controllerAdviceEnabled;
        this.exceptionAuxiliaryDataResolvingEnabled = exceptionAuxiliaryDataResolvingEnabled;
        this.convertEmptyStringsToNull = convertEmptyStringsToNull;
        this.ignoreTransientFields = ignoreTransientFields;
        this.exceptionToUnwrapList = exceptionToUnwrapList;
        this.exceptionAuxiliaryDataToIncludeInNotification = exceptionAuxiliaryDataToIncludeInNotification;
        this.defaultLocale = defaultLocale;
        this.allowedLocaleList = allowedLocaleList;
    }
}
