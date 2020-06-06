package net.croz.nrich.notification.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
public class Notification {

    private final String title;

    private final String content;

    private final List<String> messageList;

    private final NotificationSeverity severity;

}
