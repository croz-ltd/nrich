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

package net.croz.nrich.validation.constraint.util;

import org.springframework.util.ClassUtils;

import java.util.regex.Pattern;

public final class GroovyUtil {

    private GroovyUtil() {
    }

    private static final Pattern GROOVY_CLOSURE_PATTERN = Pattern.compile(".*\\$_.*closure.*");

    public static boolean isGroovyPresent() {
        return ClassUtils.isPresent("groovy.lang.MetaClass", null);
    }

    public static boolean isGroovyClosure(Class<?> type) {
        return GROOVY_CLOSURE_PATTERN.matcher(type.getName()).matches();
    }
}
