package net.croz.nrich.encrypt.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.Authentication;

import java.util.List;

@Getter
@Builder
public class EncryptionContext {

    private final String fullyQualifiedMethodName;

    private final List<Object> methodArguments;

    // same as methodArguments for decrypt operation
    private final List<Object> methodDecryptedArguments;

    private final Authentication authentication;

}
