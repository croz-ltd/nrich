package net.croz.nrich.search.util;

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

    public static String joinPath(List<String> pathList, String currentPath) {
        List<String> fullPathList = new ArrayList<>(pathList);

        fullPathList.add(currentPath);

        return String.join(PATH_SEPARATOR, fullPathList);
    }

    public static String removeFirstPathElement(String[] path) {
        return String.join(PATH_SEPARATOR, Arrays.copyOfRange(path, 1, path.length));
    }

    public static Path<?> calculateFullPath(Path<?> rootPath, String[] pathList) {
        Path<?> calculatedPath = rootPath;
        for (String currentPath : pathList) {
            calculatedPath = calculatedPath.get(currentPath);
        }

        return calculatedPath;
    }
}
