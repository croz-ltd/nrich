/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

package net.croz.nrich.security.csrf.core.service.stub;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.security.csrf.api.holder.CsrfTokenKeyHolder;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class TesCsrfTokenKeyHolder implements CsrfTokenKeyHolder {

    private final String tokenKeyName;

    private final String cryptoKeyName;

    private final Map<String, Object> dataHolderMap = new HashMap<>();

    @Override
    public String getToken() {
        return (String) dataHolderMap.get(tokenKeyName);
    }

    @Override
    public void storeToken(String csrfToken) {
        dataHolderMap.put(tokenKeyName, csrfToken);
    }

    @Override
    public Key getCryptoKey() {
        return (Key) dataHolderMap.get(cryptoKeyName);
    }

    @Override
    public void storeCryptoKey(Key cryptoKey) {
        dataHolderMap.put(cryptoKeyName, cryptoKey);
    }
}
