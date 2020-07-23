package net.croz.nrich.validation.api.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * At least one group of annotated element must contain all properties that are not null.
 */
@SuppressWarnings("unused")
@Target({ ANNOTATION_TYPE, TYPE_USE, TYPE })
@Retention(RUNTIME)
@Repeatable(ValidSearchProperties.List.class)
@Documented
@Constraint(validatedBy = {})
public @interface ValidSearchProperties {

    String message() default "{nrich.constraint.searchFields.invalid.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * List of property groups.
     * @see PropertyGroup
     *
     * @return group of properties.
     */
    PropertyGroup[] propertyGroup();

    /**
     * Holder for a list of property names
     */
    @Target(ANNOTATION_TYPE)
    @Retention(RUNTIME)
    @Documented
    @interface PropertyGroup {

        String[] value();
    }

    /**
     * Defines several {@link ValidSearchProperties} annotations on the same element.
     *
     * @see ValidSearchProperties
     */
    @Target({ ANNOTATION_TYPE, TYPE_USE, TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        ValidSearchProperties[] value();
    }
}
