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
public @interface RowKey {
    /**
     * (Optional) The name of the rowKey.
     * it has nothing to do with underlying storage layout.
     * just
     */
    String name();
}
