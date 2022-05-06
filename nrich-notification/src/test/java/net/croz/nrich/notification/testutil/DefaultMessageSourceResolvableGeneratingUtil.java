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

package net.croz.nrich.notification.testutil;

import org.springframework.context.support.DefaultMessageSourceResolvable;

public final class DefaultMessageSourceResolvableGeneratingUtil {

    private DefaultMessageSourceResolvableGeneratingUtil() {
    }

    public static DefaultMessageSourceResolvable createMessageSourceNotificationMessageResolverServiceTestRequestObjectErrorMessageSourceResolvable() {
        return createMessageSourceNotificationMessageResolverServiceTestRequestObjectErrorMessageSourceResolvable(new Object[0]);
    }

    public static DefaultMessageSourceResolvable createMessageSourceNotificationMessageResolverServiceTestRequestObjectErrorMessageSourceResolvable(Object[] arguments) {
        String[] codeList = new String[] {
            "net.croz.nrich.notification.stub.MessageSourceNotificationMessageResolverServiceTestRequest.code.invalid",
            "messageSourceNotificationMessageResolverServiceTestRequest.code.invalid", "code.invalid"
        };

        return new DefaultMessageSourceResolvable(codeList, arguments, "message");
    }

    public static DefaultMessageSourceResolvable createMessageSourceNotificationMessageResolverServiceTestRequestFieldErrorMessageSourceResolvable() {
        String[] fieldCodeList = new String[] {
            "net.croz.nrich.notification.stub.MessageSourceNotificationMessageResolverServiceTestRequest.field.code.invalid",
            "messageSourceNotificationMessageResolverServiceTestRequest.field.code.invalid", "field.code.invalid", "code.invalid"
        };

        return new DefaultMessageSourceResolvable(fieldCodeList, new Object[0], "message");
    }

    public static DefaultMessageSourceResolvable createMessageSourceNotificationMessageResolverServiceTestRequestFieldErrorLabelMessageSourceResolvable() {
        String[] fieldLabelCodeList = new String[] {
            "net.croz.nrich.notification.stub.MessageSourceNotificationMessageResolverServiceTestRequest.field.label",
            "messageSourceNotificationMessageResolverServiceTestRequest.field.label", "field.code.label"
        };

        return new DefaultMessageSourceResolvable(fieldLabelCodeList, new Object[0], "field");
    }
}
