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

package net.croz.nrich.security.csrf.webmvc.holder;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.security.csrf.api.holder.CsrfTokenKeyHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;

@RequiredArgsConstructor
public class WebMvcCsrfTokenKeyHolder implements CsrfTokenKeyHolder {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private final String tokenKeyName;

    private final String cryptoKeyName;

    @Override
    public String getToken() {
        String token = request.getHeader(tokenKeyName);

        if (token == null) {
            token = request.getParameter(tokenKeyName);
        }

        return token;
    }

    @Override
    public void storeToken(String csrfToken) {
        response.setHeader(tokenKeyName, csrfToken);
    }

    @Override
    public Key getCryptoKey() {
        return (Key) request.getSession().getAttribute(cryptoKeyName);
    }

    @Override
    public void storeCryptoKey(Key cryptoKey) {
        request.getSession().setAttribute(cryptoKeyName, cryptoKey);
    }
}
