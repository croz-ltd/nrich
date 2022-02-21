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
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated element (allowed types are MultipartFile and FilePart) must match property values resolved for content type list, allowed extension list and/or allowed regex.
 * All conditions are optional.
 */
@SuppressWarnings("unused")
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE, TYPE })
@Retention(RUNTIME)
@Repeatable(ValidFileResolvable.List.class)
@Documented
@Constraint(validatedBy = {})
public @interface ValidFileResolvable {

    String message() default "{nrich.constraint.file.invalid.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Property name from which allowed content type list is resolved (empty value allows all content types).
     *
     * @return allowed content type list
     */
    String allowedContentTypeListPropertyName() default "nrich.constraint.file.allowed-content-type-list";

    /**
     * Property name from which allowed extension list is resolved (case insensitive, empty value allows all content types).
     *
     * @return allowed extension list
     */
    String allowedExtensionListPropertyName() default "nrich.constraint.file.allowed-extension-list";

    /**
     * Property name from which allowed file name regex is resolved (empty value allows all file names).
     *
     * @return file name regex
     */
    String allowedFileNameRegexPropertyName() default "nrich.constraint.file.allowed-file-name-regex";

    /**
     * Defines several {@link ValidFileResolvable} annotations on the same element.
     *
     * @see ValidFileResolvable
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE, TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        ValidFileResolvable[] value();
    }
}
