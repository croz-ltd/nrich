package net.croz.nrich.core.api.exception;

public class EntityNotFoundException extends RuntimeException implements ExceptionWithArguments {

    private final transient Object[] argumentList;

    public EntityNotFoundException(final String message, final Object... argumentList) {
        super(message);
        this.argumentList = argumentList;
    }

    @Override
    public Object[] getArgumentList() {
        return argumentList;
    }
}
