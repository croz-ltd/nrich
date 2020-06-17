package net.croz.nrich.notification.webmvc.api.controller.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.api.service.NotificationResolverService;
import net.croz.nrich.notification.webmvc.api.controller.NotificationAwareController;

@RequiredArgsConstructor
@Getter
public class NotificationAwareControllerTestComponent implements NotificationAwareController {

    private final NotificationResolverService notificationResolverService;

}
