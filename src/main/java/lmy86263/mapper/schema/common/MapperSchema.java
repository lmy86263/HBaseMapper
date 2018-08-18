package lmy86263.mapper.schema.common;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author lmy86263
 * @date 2018/08/18
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface MapperSchema {

    String name();
}
