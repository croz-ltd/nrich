/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.notification.service;

import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.api.model.AdditionalNotificationData;
import net.croz.nrich.notification.api.model.Notification;
import net.croz.nrich.notification.api.response.NotificationDataResponse;
import net.croz.nrich.notification.api.response.NotificationResponse;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.api.service.NotificationResponseService;
import net.croz.nrich.notification.constant.NotificationConstants;
import org.springframework.validation.Errors;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UrlPathHelper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.Locale;
import java.util.Objects;

@RequiredArgsConstructor
public class WebMvcNotificationResponseService implements NotificationResponseService {

    private final NotificationResolverService notificationResolverService;

    @Override
    public NotificationResponse responseWithValidationFailureNotification(Errors errors, Class<?> validationFailedOwningType, AdditionalNotificationData additionalNotificationData) {
        Notification notification = notificationResolverService.createNotificationForValidationFailure(errors, validationFailedOwningType, additionalNotificationData);

        return new NotificationResponse(notification);
    }

    @Override
    public NotificationResponse responseWithValidationFailureNotification(ConstraintViolationException exception, AdditionalNotificationData additionalNotificationData) {
        Notification notification = notificationResolverService.createNotificationForValidationFailure(exception, additionalNotificationData);

        return new NotificationResponse(notification);
    }

    @Override
    public NotificationResponse responseWithExceptionNotification(Throwable throwable, AdditionalNotificationData additionalNotificationData) {
        Notification notification = notificationResolverService.createNotificationForException(throwable, additionalNotificationData);

        return new NotificationResponse(notification);
    }

    @Override
    public NotificationResponse responseWithNotificationActionResolvedFromRequest(AdditionalNotificationData additionalNotificationData) {
        String actionName = extractActionNameFromCurrentRequest();

        return responseWithNotification(actionName, additionalNotificationData);
    }

    @Override
    public NotificationResponse responseWithNotification(String actionName, AdditionalNotificationData additionalNotificationData) {
        Notification notification = notificationResolverService.createNotificationForAction(actionName, additionalNotificationData);

        return new NotificationResponse(notification);
    }

    @Override
    public <D> NotificationDataResponse<D> responseWithNotificationActionResolvedFromRequest(D data, AdditionalNotificationData additionalNotificationData) {
        String actionName = extractActionNameFromCurrentRequest();

        return responseWithNotification(data, actionName, additionalNotificationData);
    }

    @Override
    public <D> NotificationDataResponse<D> responseWithNotification(D data, String actionName, AdditionalNotificationData additionalNotificationData) {
        Notification notification = notificationResolverService.createNotificationForAction(actionName, additionalNotificationData);

        return new NotificationDataResponse<>(notification, data);
    }

    @Override
    public NotificationResolverService notificationResolverService() {
        return notificationResolverService;
    }

    private String extractActionNameFromCurrentRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String method = request.getMethod().toLowerCase(Locale.ROOT);

        return String.format(NotificationConstants.PREFIX_MESSAGE_FORMAT, extractActionNameFromRequest(request), method);
    }

    private String extractActionNameFromRequest(HttpServletRequest request) {
        String path = new UrlPathHelper().getPathWithinApplication(request);

        return path.substring(1).replace(NotificationConstants.REQUEST_PATH_SEPARATOR, NotificationConstants.MESSAGE_PATH_SEPARATOR);
    }
}
