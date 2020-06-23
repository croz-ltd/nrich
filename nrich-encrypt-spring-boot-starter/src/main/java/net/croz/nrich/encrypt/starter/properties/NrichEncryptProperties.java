package net.croz.nrich.encrypt.starter.properties;

import lombok.Getter;
import net.croz.nrich.encrypt.api.model.EncryptionConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.encrypt")
public class NrichEncryptProperties {

    private final List<EncryptionConfiguration> encryptionConfigurationList;

    private final List<String> ignoredMethodList;

    private final String charset;

    private final boolean encryptAspectEnabled;

    private final boolean encryptAdvisorEnabled;

    public NrichEncryptProperties(final List<EncryptionConfiguration> encryptionConfigurationList, final List<String> ignoredMethodList, @DefaultValue("UTF-8") final String charset, @DefaultValue("true") final boolean encryptAspectEnabled, @DefaultValue("true") final boolean encryptAdvisorEnabled) {
        this.encryptionConfigurationList = encryptionConfigurationList;
        this.ignoredMethodList = ignoredMethodList;
        this.charset = charset;
        this.encryptAspectEnabled = encryptAspectEnabled;
        this.encryptAdvisorEnabled = encryptAdvisorEnabled;
    }
}
