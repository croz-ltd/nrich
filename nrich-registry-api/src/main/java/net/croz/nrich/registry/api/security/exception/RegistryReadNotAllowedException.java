package net.croz.nrich.registry.api.security.exception;

public class RegistryReadNotAllowedException extends RuntimeException {

    public RegistryReadNotAllowedException(final String message) {
        super(message);
    }
}
