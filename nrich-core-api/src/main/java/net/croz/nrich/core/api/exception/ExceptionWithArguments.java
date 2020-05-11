package net.croz.nrich.core.api.exception;

public interface ExceptionWithArguments {

    @SuppressWarnings("unused")
    Object[] getArgumentList();

}
