package net.croz.nrich.registry.data.util;

import java.util.List;

public final class ClassLoadingUtil {

    private ClassLoadingUtil() {
    }

    public static Class<?> loadClassFromList(List<String> classNameList) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        Class<?> foundClass = null;
        for (String className : classNameList) {
            try {
                foundClass = Class.forName(className, true, classLoader);
            }
            catch (Exception ignored) {
                // ignored
            }

            if (foundClass != null) {
                break;
            }
        }

        return foundClass;
    }

}
