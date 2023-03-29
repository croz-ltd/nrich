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

import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PathResolvingUtil {

    private static final String PATH_SEPARATOR = ".";

    private static final String PATH_REGEX = "\\.";

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


    public static Path<?> calculateFullRestrictionPath(Path<?> rootPath, String[] pathList) {
        return calculateFullPath(rootPath, pathList, false);
    }

    public static Path<?> calculateFullSelectionPath(Path<?> rootPath, String[] pathList) {
        return calculateFullPath(rootPath, pathList, true);
    }

    private static Path<?> calculateFullPath(Path<?> rootPath, String[] pathList, boolean isSelection) {
        int lastElementIndex = pathList.length - 1;
        Path<?> calculatedPath = rootPath;

        for (int i = 0; i < pathList.length; i++) {
            if (isSelection || i == lastElementIndex) {
                calculatedPath = calculatedPath.get(pathList[i]);
            }
            else {
                calculatedPath = ((From<?, ?>) calculatedPath).join(pathList[i]);
            }
        }

        return calculatedPath;
    }
}
