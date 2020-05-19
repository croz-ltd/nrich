package net.croz.nrich.search.support;

final public class PathResolvingUtil {

    private PathResolvingUtil() {
    }

    public static String[] convertToPathList(final String path) {
        return path.split("\\.");
    }
}
