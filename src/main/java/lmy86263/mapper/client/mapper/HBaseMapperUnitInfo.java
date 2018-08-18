package lmy86263.mapper.client.mapper;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lmy86263
 * @date 2018/08/18
 */
@Setter
public class HBaseMapperUnitInfo implements MapperUnit {
    private Class<?> mapperProviderClass;
    private String mapperUnitName = "defaultMapperUnit";
    private String[] packagesToScan;
    @Getter
    private HBaseMapperMetaData metaData;

    @Override
    public Class<?> getMapperProviderClass() {
        return mapperProviderClass;
    }

    @Override
    public String getMapperUnitName() {
        return mapperUnitName;
    }

    @Override
    public String[] getPackagesToScan() {
        return packagesToScan;
    }
}
