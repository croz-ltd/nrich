package net.croz.nrich.search.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

/**
 * Annotation that indicates a projected value. Only necessary when projecting properties from assocations or embedded class. As an alternative Springs @Value annotation
 * can also be used but this one allows for specifying condition.
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Projection {

    /**
     * Association path
     *
     * @return association path
     */
    String path();

    /**
     * Condition class that decides if projection should be applied.
     *
     * @return condtion
     */
    Class<? extends Predicate<?>> condition() default DEFAULT.class;

    interface DEFAULT extends Predicate<Object> {
    }
}
