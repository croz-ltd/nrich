package net.croz.nrich.registry.data.util;

import java.util.List;

public final class ClassLoadingUtil {

    private ClassLoadingUtil() {
    }

    public static Class<?> loadClassFromList(final List<String> classNameList) {
        Class<?> foundClass = null;
        for (final String className : classNameList) {
            try {
                foundClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            }
            catch (final Exception ignored) {
            }

            if (foundClass != null) {
                break;
            }
        }

        return foundClass;
    }

}
