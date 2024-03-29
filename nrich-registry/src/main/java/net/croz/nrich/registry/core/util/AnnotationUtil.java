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

package net.croz.nrich.registry.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public final class AnnotationUtil {

    private AnnotationUtil() {
    }

    public static boolean isAnnotationPresent(Field field, String annotationName) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Annotation> annotation = (Class<? extends Annotation>) Class.forName(annotationName);

            return field.getAnnotationsByType(annotation).length > 0;
        }
        catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isAnnotationPresent(Class<?> type, String annotationName) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Annotation> annotation = (Class<? extends Annotation>) Class.forName(annotationName);

            return type.isAnnotationPresent(annotation);
        }
        catch (Exception ignored) {
            return false;
        }
    }
}
