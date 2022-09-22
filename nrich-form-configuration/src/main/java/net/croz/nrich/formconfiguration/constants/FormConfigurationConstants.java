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

package net.croz.nrich.formconfiguration.constants;

public final class FormConfigurationConstants {

    public static final String CONSTRAINT_FULL_CLIENT_MESSAGE_FORMAT = "%s.%s.client.%s.invalid";

    public static final String CONSTRAINT_FULL_MESSAGE_FORMAT = "%s.%s.%s.invalid";

    public static final String CONSTRAINT_MEDIUM_CLIENT_MESSAGE_FORMAT = "client.%s.%s.invalid";

    public static final String CONSTRAINT_MEDIUM_MESSAGE_FORMAT = "%s.%s.invalid";

    public static final String CONSTRAINT_SHORT_CLIENT_MESSAGE_FORMAT = "client.%s.invalid";

    public static final String CONSTRAINT_SHORT_MESSAGE_FORMAT = "%s.invalid";

    private FormConfigurationConstants() {
    }
}
