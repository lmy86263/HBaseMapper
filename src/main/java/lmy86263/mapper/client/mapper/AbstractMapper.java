package lmy86263.mapper.client.mapper;

/**
 * @author lmy86263
 * @date 2018/08/16
 */
public abstract class AbstractMapper implements ModelMapper {

    @Override
    public boolean validateEntity(Class<?> clazz) {
        return true;
    }

    @Override
    public void parseMappedEntity(Class<?> clazz) {

    }
}
