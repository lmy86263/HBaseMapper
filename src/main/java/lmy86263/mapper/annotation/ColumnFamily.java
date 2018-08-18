package lmy86263.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author lmy86263
 * @date 2018/08/15
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface ColumnFamily {
    /**
     * The name of the column family.
     */
    String name();
}
