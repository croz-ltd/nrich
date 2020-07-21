package net.croz.nrich.webmvc.service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultTransientPropertyResolverService implements TransientPropertyResolverService {

    private final Map<Class<?>, List<String>> typeTransientPropertyListCache = new ConcurrentHashMap<>();

    @Override
    public List<String> resolveTransientPropertyList(final Class<?> type) {
        if (!typeTransientPropertyListCache.containsKey(type)) {
            final List<String> transientPropertyList = new ArrayList<>();
            Class<?> currentType = type;

            while (currentType != Object.class) {
                final List<String> currentTransientPropertyList = Arrays.stream(currentType.getDeclaredFields())
                        .filter(field -> Modifier.isTransient(field.getModifiers()) && !field.isSynthetic())
                        .map(Field::getName)
                        .collect(Collectors.toList());

                transientPropertyList.addAll(currentTransientPropertyList);

                currentType = currentType.getSuperclass();
            }

            typeTransientPropertyListCache.put(type, transientPropertyList);
        }

        return typeTransientPropertyListCache.get(type);
    }
}
