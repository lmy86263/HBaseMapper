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
public @interface Table {

    /**
     * (Optional) The name of the table.
     * <p> Defaults to the class name.
     */
    String name();

    /**
     * (Optional) The namespace of the table.
     * <p> Defaults to the predefined namespace.
     */
    String namespace() default "default";
}
