package net.croz.nrich.webmvc.service.impl;

import net.croz.nrich.webmvc.service.TransientPropertyResolverService;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransientPropertyResolverServiceImpl implements TransientPropertyResolverService {

    private final Map<Class<?>, List<String>> transientClassCache = new ConcurrentHashMap<>();

    @Override
    public List<String> resolveTransientPropertyList(final Class<?> type) {
        if (!transientClassCache.containsKey(type)) {
            final List<String> transientFieldList = new ArrayList<>();
            Class<?> currentType = type;

            while (currentType != Object.class) {
                final List<String> currentTransientFIeldList = Arrays.stream(currentType.getDeclaredFields())
                        .filter(field -> Modifier.isTransient(field.getModifiers()) && !field.isSynthetic())
                        .map(Field::getName)
                        .collect(Collectors.toList());

                transientFieldList.addAll(currentTransientFIeldList);
                currentType = currentType.getSuperclass();
            }

            transientClassCache.put(type, transientFieldList);
        }

        return transientClassCache.get(type);
    }
}
