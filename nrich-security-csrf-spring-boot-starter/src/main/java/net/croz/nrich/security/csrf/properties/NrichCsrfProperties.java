package net.croz.nrich.security.csrf.properties;

import lombok.Getter;
import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.List;

@Getter
@ConstructorBinding
@ConfigurationProperties("nrich.security.csrf")
public class NrichCsrfProperties {

    private final boolean active;

    private final Duration tokenExpirationInterval;

    private final Duration tokenFutureThreshold;

    private final String tokenKeyName;

    private final Integer cryptoKeyLength;

    private final String initialTokenUrl;

    private final String csrfPingUrl;

    private final List<CsrfExcludeConfig> csrfExcludeConfigList;

    public NrichCsrfProperties(@DefaultValue("true") final boolean active, @DefaultValue("35m") final Duration tokenExpirationInterval, @DefaultValue("1m") final Duration tokenFutureThreshold, @DefaultValue(CsrfConstants.CSRF_TOKEN_HEADER_NAME) final String tokenKeyName, @DefaultValue("128") final Integer cryptoKeyLength, final String initialTokenUrl, @DefaultValue(CsrfConstants.CSRF_DEFAULT_PING_URI) final String csrfPingUrl, final List<CsrfExcludeConfig> csrfExcludeConfigList) {
        this.active = active;
        this.tokenExpirationInterval = tokenExpirationInterval;
        this.tokenFutureThreshold = tokenFutureThreshold;
        this.tokenKeyName = tokenKeyName;
        this.cryptoKeyLength = cryptoKeyLength;
        this.initialTokenUrl = initialTokenUrl;
        this.csrfPingUrl = csrfPingUrl;
        this.csrfExcludeConfigList = csrfExcludeConfigList;
    }
}
