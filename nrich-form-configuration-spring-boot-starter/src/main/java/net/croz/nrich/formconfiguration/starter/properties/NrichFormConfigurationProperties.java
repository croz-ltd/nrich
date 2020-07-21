package net.croz.nrich.formconfiguration.starter.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.form-configuration")
public class NrichFormConfigurationProperties {

    /**
     * Whether default converter service ({@link net.croz.nrich.formconfiguration.service.DefaultConstrainedPropertyValidatorConverterService}) for converting {@link net.croz.nrich.formconfiguration.api.model.ConstrainedProperty} instances
     * to client {@link net.croz.nrich.formconfiguration.api.model.ConstrainedPropertyClientValidatorConfiguration} list is enabled
     */
    private final boolean defaultConverterEnabled;

    public NrichFormConfigurationProperties(@DefaultValue("true") final boolean defaultConverterEnabled) {
        this.defaultConverterEnabled = defaultConverterEnabled;
    }
}
