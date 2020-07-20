package net.croz.nrich.registry.api.security.exception;

public class RegistryUpdateNotAllowedException extends RuntimeException {

    public RegistryUpdateNotAllowedException(final String message) {
        super(message);
    }
}
