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

package net.croz.nrich.notification.api.response;

import lombok.Getter;
import net.croz.nrich.notification.api.model.Notification;

/**
 * Wrapper around response data that allows for sending notification with original response data.
 *
 * @param <T> type of response data
 */
@Getter
public class NotificationDataResponse<T> extends NotificationResponse {

    /**
     * Response data
     */
    private final T data;

    public NotificationDataResponse(Notification notification, T data) {
        super(notification);
        this.data = data;
    }
}
