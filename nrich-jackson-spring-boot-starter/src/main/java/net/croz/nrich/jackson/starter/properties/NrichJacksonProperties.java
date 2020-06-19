package net.croz.nrich.jackson.starter.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.jackson")
public class NrichJacksonProperties {

    private final boolean convertEmptyStringsToNull;

    public NrichJacksonProperties(@DefaultValue("true") final boolean convertEmptyStringsToNull) {
        this.convertEmptyStringsToNull = convertEmptyStringsToNull;
    }
}
