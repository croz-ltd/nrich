package net.croz.nrich.core.api.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException implements ExceptionWithArguments {

    private final transient Object[] argumentList;

    public EntityNotFoundException(final String message, final Object... argumentList) {
        super(message);
        this.argumentList = argumentList;
    }
}
