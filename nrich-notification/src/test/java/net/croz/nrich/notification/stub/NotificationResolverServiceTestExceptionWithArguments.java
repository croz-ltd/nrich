package net.croz.nrich.notification.stub;

import lombok.Getter;

@Getter
public class NotificationResolverServiceTestExceptionWithArguments extends RuntimeException {

    private final transient Object[] argumentList;

    public NotificationResolverServiceTestExceptionWithArguments(final String message, final Object... argumentList) {
        super(message);
        this.argumentList = argumentList;
    }

}
