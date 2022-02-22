package net.croz.nrich.security.csrf.core.exception;

public class CsrfTokenException extends RuntimeException {

    public CsrfTokenException(String message) {
        super(message);
    }

    public CsrfTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
