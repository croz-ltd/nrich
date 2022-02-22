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
 * The annotated element size in bytes must be less than specified maximum.
 */
@SuppressWarnings("unused")
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(MaxSizeInBytes.List.class)
@Documented
@Constraint(validatedBy = {})
public @interface MaxSizeInBytes {

    String message() default "{nrich.constraint.maxSizeInBytes.invalid.message}";

    Class<?>[] groups() default {};

    /**
     * Maximum number of allowed bytes.
     *
     * @return maximum number of allowed bytes
     */
    int value();

    /**
     * Encoding used when resolving number bytes from string.
     *
     * @return encoding
     */
    String encoding() default "UTF-8";

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@link List} annotations on the same element.
     *
     * @see List
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        MaxSizeInBytes[] value();
    }
}
