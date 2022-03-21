package net.croz.nrich.formconfiguration.starter.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Map;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.form-configuration")
public class NrichFormConfigurationProperties {

    /**
     * Whether default converter service ({@link net.croz.nrich.formconfiguration.service.DefaultConstrainedPropertyValidatorConverterService})
     * for converting {@link net.croz.nrich.formconfiguration.api.model.ConstrainedProperty} instances
     * to client {@link net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration} list is enabled
     */
    private final boolean defaultConverterEnabled;

    /**
     * Mapping between a client side form identifier and class holding the constraints for the form (usually the class accepted as input on the server side).
     */
    private final Map<String, Class<?>> formConfigurationMapping;

    public NrichFormConfigurationProperties(@DefaultValue("true") boolean defaultConverterEnabled, Map<String, Class<?>> formConfigurationMapping) {
        this.defaultConverterEnabled = defaultConverterEnabled;
        this.formConfigurationMapping = formConfigurationMapping;
    }
}
