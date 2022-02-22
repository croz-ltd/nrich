package net.croz.nrich.validation.api.constraint;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * The annotated element must be in the specified list of values. toString method is called on annotated element and
 * it is validated against allowed list of values.
 */
@SuppressWarnings("unused")
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(InList.List.class)
@Documented
@Constraint(validatedBy = {})
public @interface InList {

    String message() default "{nrich.constraint.InList.invalid.message}";

    Class<?>[] groups() default {};

    /**
     * List of allowed values.
     *
     * @return list of allowed values
     */
    String[] value();

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@link InList} annotations on the same element.
     *
     * @see InList
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        InList[] value();
    }
}
