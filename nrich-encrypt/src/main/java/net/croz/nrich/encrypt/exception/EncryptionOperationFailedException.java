package net.croz.nrich.encrypt.exception;

/**
 * Exception thrown when encrypt or decrypt operation fails.
 */
public class EncryptionOperationFailedException extends RuntimeException {

    public EncryptionOperationFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
