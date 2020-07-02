package net.croz.nrich.notification.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
public class Notification {

    private final String title;

    private final String content;

    private final List<String> messageList;

    private final NotificationSeverity severity;

    private final Map<String, ?> uxNotificationOptions;

}
