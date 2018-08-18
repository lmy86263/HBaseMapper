package lmy86263.mapper.client.mapper;

/**
 * @author lmy86263
 * @date 2018/08/16
 */
public interface ModelMapper {

    /**
     * @param clazz
     * @return
     */
    boolean validateEntity(Class<?> clazz);

    /**
     * @param clazz
     */
    void parseMappedEntity(Class<?> clazz);
}
