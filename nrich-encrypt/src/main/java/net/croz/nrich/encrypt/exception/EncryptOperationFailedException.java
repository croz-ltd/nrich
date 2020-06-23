package net.croz.nrich.encrypt.exception;

/**
 * Exception thrown when encrypt or decrypt operation fails.
 */
public class EncryptOperationFailedException extends RuntimeException {

    public EncryptOperationFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
