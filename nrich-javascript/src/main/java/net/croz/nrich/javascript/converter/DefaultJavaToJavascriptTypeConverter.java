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

package net.croz.nrich.javascript.converter;

import net.croz.nrich.javascript.api.converter.JavaToJavascriptTypeConverter;
import net.croz.nrich.javascript.api.model.JavascriptType;
import org.springframework.core.annotation.Order;

import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Order
public class DefaultJavaToJavascriptTypeConverter implements JavaToJavascriptTypeConverter {

    private static final Map<Class<?>, JavascriptType> CLASS_JAVASCRIPT_TYPE_MAP = new HashMap<>();

    static {
        CLASS_JAVASCRIPT_TYPE_MAP.put(Boolean.class, JavascriptType.BOOLEAN);
        CLASS_JAVASCRIPT_TYPE_MAP.put(String.class, JavascriptType.STRING);
        CLASS_JAVASCRIPT_TYPE_MAP.put(Character.class, JavascriptType.STRING);
        CLASS_JAVASCRIPT_TYPE_MAP.put(Calendar.class, JavascriptType.DATE);
        CLASS_JAVASCRIPT_TYPE_MAP.put(Date.class, JavascriptType.DATE);
        CLASS_JAVASCRIPT_TYPE_MAP.put(Temporal.class, JavascriptType.DATE);
        CLASS_JAVASCRIPT_TYPE_MAP.put(Number.class, JavascriptType.NUMBER);
    }

    @Override
    public boolean supports(Class<?> type) {
        return true;
    }

    @Override
    public String convert(Class<?> type) {
        return CLASS_JAVASCRIPT_TYPE_MAP.entrySet().stream()
            .filter(entry -> entry.getKey().isAssignableFrom(type))
            .findFirst()
            .map(Map.Entry::getValue)
            .orElse(JavascriptType.OBJECT)
            .name().toLowerCase(Locale.ROOT);
    }
}
