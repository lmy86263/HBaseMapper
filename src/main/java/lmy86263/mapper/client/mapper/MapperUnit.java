package lmy86263.mapper.client.mapper;

/**
 * @author lmy86263
 * @date 2018/08/17
 */
public interface MapperUnit {

    Class<?> getMapperProviderClass();

    String getMapperUnitName();

    String[] getPackagesToScan();

}
