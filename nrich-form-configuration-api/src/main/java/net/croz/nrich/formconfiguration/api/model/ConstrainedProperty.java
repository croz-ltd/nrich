package net.croz.nrich.formconfiguration.api.model;

import lombok.Builder;
import lombok.Getter;

import javax.validation.metadata.ConstraintDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public Map<String, Object> getConstraintArgumentMap() {
        final List<String> ignoredKeyList = Arrays.asList("groups", "message", "payload");

        return constraintDescriptor.getAttributes().entrySet().stream()
                .filter(entry -> !ignoredKeyList.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Object[] getConstraintArgumentList() {
        return getConstraintArgumentMap().values().toArray();
    }

    public String getConstraintMessage() {
        return constraintDescriptor.getMessageTemplate();
    }
}
