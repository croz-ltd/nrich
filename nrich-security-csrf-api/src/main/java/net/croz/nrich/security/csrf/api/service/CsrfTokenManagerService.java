package net.croz.nrich.security.csrf.api.service;

import net.croz.nrich.security.csrf.api.holder.CsrfTokenHolder;

public interface CsrfTokenManagerService {

    void validateAndRefreshToken(CsrfTokenHolder csrfTokenHolder);

    String generateToken(CsrfTokenHolder csrfTokenHolder);

}
