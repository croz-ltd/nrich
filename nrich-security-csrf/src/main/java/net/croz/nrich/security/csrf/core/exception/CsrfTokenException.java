package net.croz.nrich.security.csrf.core.exception;

public class CsrfTokenException extends RuntimeException {

    public CsrfTokenException(final String message) {
        super(message);
    }

    public CsrfTokenException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
