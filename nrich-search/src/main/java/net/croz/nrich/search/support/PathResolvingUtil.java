package net.croz.nrich.search.support;

public final class PathResolvingUtil {

    private PathResolvingUtil() {
    }

    public static String[] convertToPathList(final String path) {
        return path.split("\\.");
    }
}
