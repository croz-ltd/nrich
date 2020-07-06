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

    String[] allowedContentTypeList() default { };

    String[] allowedExtensionList() default { };

    String allowedFileNameRegex() default "";

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE, TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        ValidFile[] value();
    }
}
