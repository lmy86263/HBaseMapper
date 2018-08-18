package lmy86263.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author lmy86263
 * @date 2018/08/15
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Namespace {

    /**
     * (mandatory) The name of the namespace.
     */
    String name();
}
