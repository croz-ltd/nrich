/*
 *  Copyright 2020-2022 CROZ d.o.o, the original author or authors.
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

package net.croz.nrich.notification.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Holder for notification data.
 */
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Getter
public class Notification {

    /**
     * Title of the notification.
     */
    private final String title;

    /**
     * Content of the notification (i.e. for exception notification this will contain resolved message for exception or 'Error occurred' text).
     */
    private final String content;

    /**
     * List of messages (i.e. for validation failure notification this will contain all validation failure messages).
     */
    private final List<String> messageList;

    /**
     * Severity indicating importance of notification.
     */
    private final NotificationSeverity severity;

    /**
     * Additional ux notification options (i.e. autoHide etc.).
     */
    private final Map<String, ?> uxNotificationOptions;

}
