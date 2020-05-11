package net.croz.nrich.notification.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.croz.nrich.notification.controller.NotificationAwareController;
import net.croz.nrich.notification.service.NotificationResolverService;

@Getter
@RequiredArgsConstructor
public class NotificationAwareControllerTestComponent implements NotificationAwareController {

    private final NotificationResolverService notificationResolverService;

}
