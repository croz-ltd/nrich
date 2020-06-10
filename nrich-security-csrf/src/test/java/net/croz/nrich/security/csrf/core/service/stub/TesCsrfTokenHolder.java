package net.croz.nrich.security.csrf.core.service.stub;

import net.croz.nrich.security.csrf.core.holder.CsrfTokenHolder;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class TesCsrfTokenHolder implements CsrfTokenHolder {

    private final Map<String, Object> dataHolderMap = new HashMap<>();

    @Override
    public String getToken(final String tokenKeyName) {
        return (String) dataHolderMap.get(tokenKeyName);
    }

    @Override
    public void storeToken(final String tokenKeyName, final String csrfToken) {
        dataHolderMap.put(tokenKeyName, csrfToken);
    }

    @Override
    public Key getCryptoKey(final String cryptoKeyName) {
        return (Key) dataHolderMap.get(cryptoKeyName);
    }

    @Override
    public void storeCryptoKey(final String cryptoKeyName, final Key cryptoKey) {
        dataHolderMap.put(cryptoKeyName, cryptoKey);
    }
}
