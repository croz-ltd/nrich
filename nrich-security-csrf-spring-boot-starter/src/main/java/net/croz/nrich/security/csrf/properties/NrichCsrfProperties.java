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

import net.croz.nrich.security.csrf.core.constants.CsrfConstants;
import net.croz.nrich.security.csrf.core.model.CsrfExcludeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.List;

/**
 * @param active                  Whether CSRF is active.
 * @param tokenExpirationInterval Duration of CSRF token.
 * @param tokenFutureThreshold    Duration of how long token can be in the future (can happen when server and client time is not in sync).
 * @param tokenKeyName            Name of CSRF token.
 * @param cryptoKeyLength         Length of crypto key (128, 256...).
 * @param initialTokenUrl         Initial application url (i.e. url that user is redirected after login). Token will be added to response from this url as <pre>csrfInitialToken</pre> parameter.
 * @param csrfPingUri             Uri used for CSRF ping request.
 * @param csrfExcludeConfigList   A list of {@link CsrfExcludeConfig} instances that contain urls or regexps excluded from CSRF check.
 */
@ConfigurationProperties("nrich.security.csrf")
public record NrichCsrfProperties(@DefaultValue("true") boolean active, @DefaultValue("35m") Duration tokenExpirationInterval, @DefaultValue("1m") Duration tokenFutureThreshold,
                                  @DefaultValue("X-CSRF-Token") String tokenKeyName, @DefaultValue("128") Integer cryptoKeyLength, String initialTokenUrl,
                                  @DefaultValue(CsrfConstants.CSRF_DEFAULT_PING_URI) String csrfPingUri, List<CsrfExcludeConfig> csrfExcludeConfigList) {

}
