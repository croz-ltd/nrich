package net.croz.nrich.formconfiguration.api.model;

import lombok.Builder;
import lombok.Getter;

import javax.validation.metadata.ConstraintDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class ConstrainedProperty {

    private final Class<?> parentType;

    private final Class<?> type;

    private final String name;

    private final String path;

    private final ConstraintDescriptor<?> constraintDescriptor;

    public String getConstraintName() {
        return constraintDescriptor.getAnnotation().annotationType().getSimpleName();
    }

    public Object[] getConstraintArgumentList() {
        final List<String> ignoredKeyList = Arrays.asList("groups", "message", "payload");

        return constraintDescriptor.getAttributes().entrySet().stream()
                .filter(entry -> !ignoredKeyList.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .toArray();
    }
}
