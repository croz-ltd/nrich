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
