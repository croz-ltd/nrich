package net.croz.nrich.security.csrf.core.holder;

import java.security.Key;

public interface CsrfTokenHolder {

    String getToken(String tokenKeyName);

    void storeToken(String tokenKeyName, String csrfToken);

    Key getCryptoKey(String cryptoKeyName);

    void storeCryptoKey(String cryptoKeyName, Key cryptoKey);

}
