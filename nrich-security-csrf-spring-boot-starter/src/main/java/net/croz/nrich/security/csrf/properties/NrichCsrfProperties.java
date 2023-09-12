/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.security.csrf.properties;

import lombok.Getter;
import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.List;

@Getter
@ConfigurationProperties("nrich.security.csrf")
public class NrichCsrfProperties {

    /**
     * Whether CSRF is active.
     */
    private final boolean active;

    /**
     * Duration of CSRF token.
     */
    private final Duration tokenExpirationInterval;

    /**
     * Duration of how long token can be in the future (can happen when server and client time is not in sync).
     */
    private final Duration tokenFutureThreshold;

    /**
     * Name of CSRF token.
     */
    private final String tokenKeyName;

    /**
     * Length of crypto key (128, 256...).
     */
    private final Integer cryptoKeyLength;

    /**
     * Initial application url (i.e. url that user is redirected after login). Token will be added to response from this url as <pre>csrfInitialToken</pre> parameter.
     */
    private final String initialTokenUrl;

    /**
     * Uri used for CSRF ping request.
     */
    private final String csrfPingUri;

    /**
     * A list of {@link CsrfExcludeConfig} instances that contain urls or regexps excluded from CSRF check.
     */
    private final List<CsrfExcludeConfig> csrfExcludeConfigList;

    public NrichCsrfProperties(@DefaultValue("true") boolean active, @DefaultValue("35m") Duration tokenExpirationInterval, @DefaultValue("1m") Duration tokenFutureThreshold,
                               @DefaultValue("X-CSRF-Token") String tokenKeyName, @DefaultValue("128") Integer cryptoKeyLength, String initialTokenUrl,
                               @DefaultValue(CsrfConstants.CSRF_DEFAULT_PING_URI) String csrfPingUri, List<CsrfExcludeConfig> csrfExcludeConfigList) {
        this.active = active;
        this.tokenExpirationInterval = tokenExpirationInterval;
        this.tokenFutureThreshold = tokenFutureThreshold;
        this.tokenKeyName = tokenKeyName;
        this.cryptoKeyLength = cryptoKeyLength;
        this.initialTokenUrl = initialTokenUrl;
        this.csrfPingUri = csrfPingUri;
        this.csrfExcludeConfigList = csrfExcludeConfigList;
    }
}
