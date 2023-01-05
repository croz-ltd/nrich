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

package net.croz.nrich.notification.api.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Additional notification data used to configure notification.
 */
@Getter
@Builder
public class AdditionalNotificationData {

    /**
     * Overridden notification severity. If not specified notification will have severity depending on method
     * that was used to generate (i.e NotificationSeverity.ERROR for createNotificationForException).
     */
    private final NotificationSeverity severity;

    /**
     * Additional data for which message will be resolved from {@link org.springframework.context.MessageSource} and added to Notification messageList.
     * Messages are resolved from key <pre>notification.additional-data.mapKey.message</pre> and value is passed in as argument when message resolving is performed.
     */
    private final Map<String, ?> messageListDataMap;

    /**
     * Notification ux options. Can contain anything that client can interpret.
     */
    private final Map<String, ?> uxNotificationOptions;

    public static AdditionalNotificationData empty() {
        return AdditionalNotificationData.builder().build();
    }
}
