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

package net.croz.nrich.javascript.converter;

import net.croz.nrich.javascript.api.converter.JavaToJavascriptTypeConverter;
import net.croz.nrich.javascript.api.model.JavascriptType;
import org.springframework.core.annotation.Order;

import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@Order
public class DefaultJavaToJavascriptTypeConverter implements JavaToJavascriptTypeConverter {

    private static final Map<Class<?>, JavascriptType> CLASS_JAVASCRIPT_TYPE_MAP = Map.of(
        Boolean.class, JavascriptType.BOOLEAN,
        String.class, JavascriptType.STRING,
        Character.class, JavascriptType.STRING,
        Calendar.class, JavascriptType.DATE,
        Date.class, JavascriptType.DATE,
        Temporal.class, JavascriptType.DATE,
        Number.class, JavascriptType.NUMBER,
        Enum.class, JavascriptType.STRING,
        Collection.class, JavascriptType.ARRAY
    );

    @Override
    public boolean supports(Class<?> type) {
        return true;
    }

    @Override
    public String convert(Class<?> type) {
        JavascriptType javascriptType;
        if (type.isArray()) {
            javascriptType = JavascriptType.ARRAY;
        }
        else {
            javascriptType = CLASS_JAVASCRIPT_TYPE_MAP.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(type))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(JavascriptType.OBJECT);
        }

        return javascriptType.name().toLowerCase(Locale.ROOT);
    }
}
