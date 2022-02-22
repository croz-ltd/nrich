package net.croz.nrich.registry.api.security.exception;

/**
 * Exception thrown when registry create, update or delete is not allowed.
 */
public class RegistryUpdateNotAllowedException extends RuntimeException {

    public RegistryUpdateNotAllowedException(String message) {
        super(message);
    }
}
