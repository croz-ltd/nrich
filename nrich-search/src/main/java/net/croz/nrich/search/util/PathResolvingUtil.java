package net.croz.nrich.search.util;

import javax.persistence.criteria.Path;

public final class PathResolvingUtil {

    private static final String PATH_REGEX = "\\.";

    private PathResolvingUtil() {
    }

    public static String[] convertToPathList(String path) {
        return path.split(PATH_REGEX);
    }

    public static Path<?> calculateFullPath(Path<?> rootPath, String[] pathList) {
        Path<?> calculatedPath = rootPath;
        for (String currentPath : pathList) {
            calculatedPath = calculatedPath.get(currentPath);
        }

        return calculatedPath;
    }
}
