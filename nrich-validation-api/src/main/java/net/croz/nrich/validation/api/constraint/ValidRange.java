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
 * Annotated element from property must be less than (or equal to if inclusive is true) to to property.
 */
@SuppressWarnings("unused")
@Target({ ANNOTATION_TYPE, TYPE_USE, TYPE })
@Retention(RUNTIME)
@Repeatable(ValidRange.List.class)
@Documented
@Constraint(validatedBy = {})
public @interface ValidRange {

    String message() default "{nrich.constraint.range.invalid.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * Name of from property.
     *
     * @return name of from property
     */
    String fromPropertyName();

    /**
     * Name of to property.
     *
     * @return name of to property
     */
    String toPropertyName();

    /**
     * Whether from property can be equal to to property.
     *
     * @return whether property from can be equal to to property
     */
    boolean inclusive() default false;

    /**
     * Defines several {@link ValidRange} annotations on the same element.
     *
     * @see ValidRange
     */
    @Target({ ANNOTATION_TYPE, TYPE_USE, TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        ValidRange[] value();
    }
}
