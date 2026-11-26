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

package net.croz.nrich.search.util;

import org.hibernate.query.criteria.JpaFrom;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class PathResolvingUtil {

    private static final String PATH_SEPARATOR = ".";

    private static final String PATH_REGEX = "\\.";

    // Hibernate will throw an exception if trying to find as an attribute but it can be used as an condition and as an projection
    private static final String CLASS_ATTRIBUTE_NAME = "class";

    private PathResolvingUtil() {
    }

    public static String[] convertToPathList(String path) {
        return path.split(PATH_REGEX);
    }

    public static String joinPath(String... pathList) {
        return String.join(PATH_SEPARATOR, pathList);
    }

    public static String joinPath(List<String> pathList, String currentPath) {
        List<String> fullPathList = new ArrayList<>(pathList);

        fullPathList.add(currentPath);

        return String.join(PATH_SEPARATOR, fullPathList);
    }

    public static String removeFirstPathElement(String[] path) {
        return String.join(PATH_SEPARATOR, Arrays.copyOfRange(path, 1, path.length));
    }

    public static Path<?> calculateFullPath(Path<?> rootPath, JoinType defaultJoinType, String[] pathList) {
        JoinType calculatedJoinPath = Optional.ofNullable(defaultJoinType).orElse(JoinType.INNER);
        Path<?> calculatedPath = rootPath;

        for (String currentPathSegment : pathList) {
            Attribute<?, ?> attribute = null;
            EntityType<?> entityType = null;
            if (!CLASS_ATTRIBUTE_NAME.equals(currentPathSegment) && calculatedPath.getModel() instanceof EntityType<?> pathEntityType) {
                entityType = pathEntityType;
                attribute = AttributeResolvingUtil.resolveAttributeByName(entityType, currentPathSegment);
            }

            if (attribute == null) {
                calculatedPath = calculatedPath.get(currentPathSegment);
            }
            else if (shouldJoinPath(attribute)) {
                From<?, ?> from = (From<?, ?>) castPathIfNecessary(calculatedPath, entityType, attribute);

                calculatedPath = from.getJoins().stream()
                    .filter(join -> currentPathSegment.equals(join.getAttribute().getName()))
                    .findFirst()
                    .orElseGet(() -> from.join(currentPathSegment, calculatedJoinPath));
            }
            else {
                calculatedPath = castPathIfNecessary(calculatedPath, entityType, attribute).get(currentPathSegment);
            }
        }

        return calculatedPath;
    }

    public static <T extends Path<?>> T castPathIfNecessary(T path, String attributeName) {
        if (CLASS_ATTRIBUTE_NAME.equals(attributeName) || !(path.getModel() instanceof EntityType<?> entityType)) {
            return path;
        }

        Attribute<?, ?> attribute = AttributeResolvingUtil.resolveAttributeByName(entityType, attributeName);
        if (attribute == null) {
            return path;
        }

        @SuppressWarnings("unchecked")
        T castedPath = (T) castPathIfNecessary(path, entityType, attribute);

        return castedPath;
    }

    private static boolean shouldJoinPath(Attribute<?, ?> attribute) {
        return attribute.isCollection() || attribute.isAssociation();
    }

    private static <S> Path<?> castPathIfNecessary(Path<?> path, ManagedType<?> managedType, Attribute<?, ?> attribute) {
        if (!(path instanceof JpaFrom<?, ?>)) {
            return path;
        }

        @SuppressWarnings("unchecked")
        Class<S> declaringJavaType = (Class<S>) attribute.getDeclaringType().getJavaType();
        if (declaringJavaType.isAssignableFrom(managedType.getJavaType())) {
            return path;
        }

        @SuppressWarnings("unchecked")
        JpaFrom<?, S> from = (JpaFrom<?, S>) path;

        return from.treatAs(declaringJavaType);
    }
}
