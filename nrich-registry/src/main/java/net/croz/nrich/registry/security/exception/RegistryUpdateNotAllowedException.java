package net.croz.nrich.registry.security.exception;

public class RegistryUpdateNotAllowedException extends RuntimeException {

    public RegistryUpdateNotAllowedException(final String message) {
        super(message);
    }
}
