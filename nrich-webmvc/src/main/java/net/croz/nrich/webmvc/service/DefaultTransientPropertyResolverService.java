package net.croz.nrich.webmvc.service;

import org.springframework.cache.annotation.Cacheable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultTransientPropertyResolverService implements TransientPropertyResolverService {

    @Cacheable("nrich.transientPropertyResolver.cache")
    @Override
    public List<String> resolveTransientPropertyList(Class<?> type) {
        List<String> transientPropertyList = new ArrayList<>();
        Class<?> currentType = type;

        while (currentType != Object.class) {
            List<String> currentTransientPropertyList = Arrays.stream(currentType.getDeclaredFields())
                .filter(field -> Modifier.isTransient(field.getModifiers()) && !field.isSynthetic())
                .map(Field::getName)
                .collect(Collectors.toList());

            transientPropertyList.addAll(currentTransientPropertyList);

            currentType = currentType.getSuperclass();
        }

        return transientPropertyList;
    }
}
