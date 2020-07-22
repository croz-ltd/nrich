package net.croz.nrich.encrypt.api.model;

import lombok.Builder;
import lombok.Getter;

import java.security.Principal;
import java.util.List;

/**
 * Context in which encryption operation is performed
 */
@Getter
@Builder
public class EncryptionContext {

    /**
     * Name of the method to encrypt/decrypt (in following form: fullyQualifiedClassName.methodName).
     */
    private final String fullyQualifiedMethodName;

    /**
     * Method arguments.
     */
    private final List<Object> methodArguments;

    /**
     * Decrypted method arguments (same as methodArguments for decrypt operation).
     */
    private final List<Object> methodDecryptedArguments;

    /**
     * Current user {@link Principal}.
     */
    private final Principal principal;

}
