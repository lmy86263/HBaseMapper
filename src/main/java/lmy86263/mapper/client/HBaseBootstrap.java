package lmy86263.mapper.client;

import com.google.common.base.Preconditions;
import lmy86263.mapper.annotation.Table;
import lmy86263.mapper.client.mapper.DefaultHBaseMapper;
import lmy86263.mapper.client.mapper.HBaseMapperMetaData;
import lmy86263.mapper.client.mapper.HBaseMapperUnitInfo;
import lmy86263.mapper.client.mapper.ModelMapper;
import lmy86263.mapper.client.model.TableDefinition;
import lmy86263.mapper.configure.SystemParams;
import lmy86263.mapper.exception.HBaseMapperException;
import lmy86263.mapper.schema.HBaseSchemaManager;
import lmy86263.mapper.schema.common.MapperSchema;
import lmy86263.mapper.schema.common.SchemaManager;
import lmy86263.mapper.schema.common.Schemas;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author lmy86263
 * @date 2018/08/15
 */
@Slf4j
public class HBaseBootstrap {
    private Properties props;
    private ConnectionManager connectionManager;
    private SchemaManager schemaManager;
    private ModelMapper modelMapper;
    private HBaseMapperUnitInfo mapperUnit;

    public HBaseBootstrap() {
        this.props = new Properties();
        try {
            InputStream defaultConfig = this.getClass().getClassLoader().getResourceAsStream("HBaseMapper.properties");
            if (Objects.nonNull(defaultConfig)) {
                props.load(defaultConfig);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
        initConnManager();
    }

    public HBaseBootstrap(Properties props) {
        Preconditions.checkNotNull(props, "initial parameters for HBase cannot be null");
        Preconditions.checkArgument(!props.isEmpty(), "initial parameters for HBase cannot be empty");
        this.props = props;
    }

    public void init() {
        if (System.getProperty(SystemParams.OS_NAME).contains(SystemParams.OS.WINDOWS.toString())) {
            System.setProperty(SystemParams.HADOOP_HOME, this.props.getProperty(SystemParams.HADOOP_HOME));
        }
        String hbaseVersion = Optional.ofNullable(this.props.getProperty(SystemParams.HBASE_SERVER_VERSION)).orElse(SystemParams.V2);
        System.setProperty(SystemParams.HBASE_SERVER_VERSION, hbaseVersion);

        mapperUnit = new HBaseMapperUnitInfo();
        mapperUnit.setMapperProviderClass(DefaultHBaseMapper.class);
        log.info("initial work completed");
    }

    public void initConnManager() {
        connectionManager = new ConnectionManager(props);
    }

    public void setParameter(String param, String value) {
        props.setProperty(param, value);
    }

    public void setPackagesToScan(String... packagesToScan) {
        mapperUnit.setPackagesToScan(packagesToScan);
    }

    public Set<Class<?>> scanPackage(String... pkg) {
        Reflections reflector = new Reflections(pkg);
        Set<Class<?>> mappedClasses = new HashSet<>();
        mappedClasses.addAll(reflector.getTypesAnnotatedWith(Table.class));
        return mappedClasses;
    }

    public void loadMapperInfo() {
        Set<Class<?>> clazzs = scanPackage(mapperUnit.getPackagesToScan());
        List<TableDefinition> tables = new LinkedList<>();

        DefaultHBaseMapper provider = null;
        try {
            provider = (DefaultHBaseMapper) mapperUnit.getMapperProviderClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new HBaseMapperException("get HBase mapper provider failed", e);
        }

        for (Class<?> clazz : clazzs) {
            provider.parseMappedEntity(clazz);
            tables.add(provider.getTableInfo());
        }

        HBaseMapperMetaData metaData = new HBaseMapperMetaData();
        metaData.setTables(tables);
        mapperUnit.setMetaData(metaData);
    }

    public void start() {
        loadMapperInfo();
        refreshSchema();
    }

    public void refreshSchema() {
        String schema = Optional.ofNullable(props.getProperty(SystemParams.HBASE_MAPPER_SCHEMA)).orElse(Schemas.CREATE);
        schemaManager = new HBaseSchemaManager(connectionManager.getConnectionWrapper(), mapperUnit.getMetaData());

        Method schemaHandler = Arrays.stream(HBaseSchemaManager.class.getMethods()).filter(method -> method.getAnnotation(MapperSchema.class) != null)
                .filter(method -> method.getAnnotation(MapperSchema.class).name().equals(schema)).findAny().get();
        try {
            schemaHandler.invoke(schemaManager);
        } catch (Exception e) {
            throw new HBaseMapperException("instantiate HBaseSchemaManager failed", e);
        }
    }
}
