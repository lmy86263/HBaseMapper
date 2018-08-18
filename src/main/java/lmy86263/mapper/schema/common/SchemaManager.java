package lmy86263.mapper.schema.common;

/**
 * @author lmy86263
 * @date 2018/08/15
 */
public interface SchemaManager {

    void createSchema();

    void updateSchema();

    /**
     * The schema is not recommended in productive environment, mostly.
     * It mostly can be used in development for testing
     */
    void createOrDropSchema();

}
