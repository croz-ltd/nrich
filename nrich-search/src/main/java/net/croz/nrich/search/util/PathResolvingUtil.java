package net.croz.nrich.search.util;

import javax.persistence.criteria.Path;

public final class PathResolvingUtil {

    private PathResolvingUtil() {
    }

    public static String[] convertToPathList(final String path) {
        return path.split("\\.");
    }

    public static Path<?> calculateFullPath(final Path<?> rootPath, final String[] pathList) {
        Path<?> calculatedPath = rootPath;
        for (final String currentPath : pathList) {
            calculatedPath = calculatedPath.get(currentPath);
        }

        return calculatedPath;
    }
}
