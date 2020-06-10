package net.croz.nrich.security.csrf.core.service;

import net.croz.nrich.security.csrf.core.holder.CsrfTokenHolder;

public interface CsrfTokenManagerService {

    void handleCsrfToken(CsrfTokenHolder csrfTokenHolder);

    String generateToken(CsrfTokenHolder csrfTokenHolder);

}
