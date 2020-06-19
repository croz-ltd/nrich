package net.croz.nrich.formconfiguration.starter.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.form-configuration")
public class FormConfigurationProperties {

    private final boolean defaultConverterEnabled;

    public FormConfigurationProperties(@DefaultValue("true") final boolean defaultConverterEnabled) {
        this.defaultConverterEnabled = defaultConverterEnabled;
    }
}
