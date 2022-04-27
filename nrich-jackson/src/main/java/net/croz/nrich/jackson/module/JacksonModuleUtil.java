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

package net.croz.nrich.jackson.module;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.croz.nrich.jackson.deserializer.ConvertEmptyStringToNullDeserializer;
import net.croz.nrich.jackson.serializer.EntityClassSerializerModifier;

import java.util.List;

public final class JacksonModuleUtil {

    public static final String CONVERT_EMPTY_STRING_TO_NULL_MODULE_NAME = "convertEmptyStringToNullModule";

    public static final String CLASS_NAME_SERIALIZER_MODULE = "classNameSerializerModule";

    private JacksonModuleUtil() {
    }

    public static Module convertEmptyStringToNullModule() {
        SimpleModule simpleModule = new SimpleModule(JacksonModuleUtil.CONVERT_EMPTY_STRING_TO_NULL_MODULE_NAME);

        simpleModule.addDeserializer(String.class, new ConvertEmptyStringToNullDeserializer());

        return simpleModule;
    }

    public static Module classNameSerializerModule(boolean serializeEntityAnnotatedClasses, List<String> packageList) {
        SimpleModule simpleModule = new SimpleModule(JacksonModuleUtil.CLASS_NAME_SERIALIZER_MODULE);

        simpleModule.setSerializerModifier(new EntityClassSerializerModifier(serializeEntityAnnotatedClasses, packageList));

        return simpleModule;
    }
}
