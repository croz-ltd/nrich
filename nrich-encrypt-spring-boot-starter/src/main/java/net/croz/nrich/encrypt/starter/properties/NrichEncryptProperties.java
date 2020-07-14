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

    private final String textEncryptCharset;

    private final boolean encryptAspectEnabled;

    private final boolean encryptAdvisorEnabled;

    private final String encryptPassword;

    private final String encryptSalt;

    public NrichEncryptProperties(final List<EncryptionConfiguration> encryptionConfigurationList, final List<String> ignoredMethodList, @DefaultValue("UTF-8") final String textEncryptCharset, @DefaultValue("true") final boolean encryptAspectEnabled, @DefaultValue("true") final boolean encryptAdvisorEnabled, final String encryptPassword, final String encryptSalt) {
        this.encryptionConfigurationList = encryptionConfigurationList;
        this.ignoredMethodList = ignoredMethodList;
        this.textEncryptCharset = textEncryptCharset;
        this.encryptAspectEnabled = encryptAspectEnabled;
        this.encryptAdvisorEnabled = encryptAdvisorEnabled;
        this.encryptPassword = encryptPassword;
        this.encryptSalt = encryptSalt;
    }
}
