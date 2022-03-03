package net.croz.nrich.webmvc.advice.stub;

import lombok.Getter;
import net.croz.nrich.core.api.exception.ExceptionWithArguments;

@Getter
public class NotificationErrorHandlingRestControllerAdviceTestExceptionWithArguments extends RuntimeException implements ExceptionWithArguments {

    private final transient Object[] argumentList;

    public NotificationErrorHandlingRestControllerAdviceTestExceptionWithArguments(String message, Object... argumentList) {
        super(message);
        this.argumentList = argumentList;
    }
}
