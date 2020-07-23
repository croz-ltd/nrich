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
 * Annotated element (allowed types are MultipartFile and FilePart) must match specified content type list, allowed extension list and/or allowed regex.
 * All conditions are optional.
 */
@SuppressWarnings("unused")
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE, TYPE })
@Retention(RUNTIME)
@Repeatable(ValidFile.List.class)
@Documented
@Constraint(validatedBy = {})
public @interface ValidFile {

    String message() default "{nrich.constraint.file.invalid.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * Allowed content type list (empty value allows all content types).
     *
     * @return allowed content type list
     */
    String[] allowedContentTypeList() default { };

    /**
     * Allowed extension list (case insensitive, empty value allows all content types).
     *
     * @return allowed extension list
     */
    String[] allowedExtensionList() default { };

    /**
     * Allowed file name regex (empty value allows all file names).
     *
     * @return file name regex
     */
    String allowedFileNameRegex() default "";

    /**
     * Defines several {@link ValidFile} annotations on the same element.
     *
     * @see ValidFile
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE, TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        ValidFile[] value();
    }
}
