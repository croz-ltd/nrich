package net.croz.nrich.registry.security.exception;

public class RegistryReadNotAllowedException extends RuntimeException {

    public RegistryReadNotAllowedException(final String message) {
        super(message);
    }
}
