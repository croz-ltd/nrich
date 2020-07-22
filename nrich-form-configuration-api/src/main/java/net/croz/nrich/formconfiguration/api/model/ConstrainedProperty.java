package net.croz.nrich.formconfiguration.api.model;

import lombok.Builder;
import lombok.Getter;

import javax.validation.metadata.ConstraintDescriptor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a combination of property and constraint (single) defined on it. It contains all information about specific property, class where it is defined and defined constraint.
 */
@Builder
@Getter
public class ConstrainedProperty {

    /**
     * Class where this property is defined.
     */
    private final Class<?> parentType;

    /**
     * Type of property.
     */
    private final Class<?> type;

    /**
     * Property name.
     */
    private final String name;

    /**
     * Property path relating to parentType.
     */
    private final String path;

    /**
     * Constraint information
     */
    private final ConstraintDescriptor<?> constraintDescriptor;

    /**
     * Returns constraint annotation name (i.e for @NotNull constraint it will return NotNull)
     * @return constraint name
     */
    public String getConstraintName() {
        return constraintDescriptor.getAnnotation().annotationType().getSimpleName();
    }

    /**
     * Returns constraint arguments as a map where key is argument name and value is argument value.
     *
     * @return containing argument map
     */
    public Map<String, Object> getConstraintArgumentMap() {
        final List<String> ignoredKeyList = Arrays.asList("groups", "message", "payload");

        return constraintDescriptor.getAttributes().entrySet().stream()
                .filter(entry -> !ignoredKeyList.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Returns constraint arguments as a list.
     *
     * @return constraint arguments as a list
     */
    public Object[] getConstraintArgumentList() {
        return getConstraintArgumentMap().values().toArray();
    }

    /**
     * Returns default constraint message.
     *
     * @return default constraint message
     */
    public String getConstraintMessage() {
        return constraintDescriptor.getMessageTemplate();
    }
}
