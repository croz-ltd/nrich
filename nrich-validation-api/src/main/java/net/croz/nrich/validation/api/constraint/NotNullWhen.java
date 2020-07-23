package net.croz.nrich.validation.api.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.function.Predicate;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated element property must not be null when condition is satisfied.
 */
@SuppressWarnings("unused")
@Target({ ANNOTATION_TYPE, TYPE_USE, TYPE })
@Retention(RUNTIME)
@Repeatable(NotNullWhen.List.class)
@Documented
@Constraint(validatedBy = {})
public @interface NotNullWhen {

    String message() default "{nrich.constraint.notNullWhen.invalid.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * Property name that must not be null.
     *
     * @return property name
     */
    String property();

    /**
     * Condition that if satisfied requires property not to be null.
     *
     * @return condition
     */
    Class<? extends Predicate<?>> condition();

    /**
     * Defines several {@link NotNullWhen} annotations on the same element.
     *
     * @see NotNullWhen
     */
    @Target({ ANNOTATION_TYPE, TYPE_USE, TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        NotNullWhen[] value();
    }
}
