package net.croz.nrich.validation.starter.properties;

import lombok.Getter;
import net.croz.nrich.validation.constraint.validator.ValidFileValidatorProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.validation")
public class NrichValidationProperties {

    private final ValidFileValidatorProperties fileValidation;

    public NrichValidationProperties(@DefaultValue final ValidFileValidatorProperties fileValidation) {
        this.fileValidation = fileValidation;
    }
}
