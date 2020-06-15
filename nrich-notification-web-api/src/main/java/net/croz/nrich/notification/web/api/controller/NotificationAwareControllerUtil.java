package net.croz.nrich.notification.web.api.controller;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public final class NotificationAwareControllerUtil {

    private NotificationAwareControllerUtil() {
    }

    public static String extractActionNameFromCurrentRequest() {
        final HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        return extractActionNameFromRequest(request);
    }

    public static String extractActionNameFromRequest(final HttpServletRequest request) {
        final String path = new UrlPathHelper().getPathWithinApplication(request);

        return path.substring(1).replace("/", ".");
    }
}
