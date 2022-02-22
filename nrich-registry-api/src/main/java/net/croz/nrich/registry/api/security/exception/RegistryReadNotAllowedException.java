package net.croz.nrich.registry.api.security.exception;

/**
 * Exception thrown when registry read is not allowed.
 */
public class RegistryReadNotAllowedException extends RuntimeException {

    public RegistryReadNotAllowedException(String message) {
        super(message);
    }
}
