package net.croz.nrich.search.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Projection {

    String path();

    Class<? extends Predicate<?>> condition() default DEFAULT.class;

    interface DEFAULT extends Predicate<Object> {}

}
