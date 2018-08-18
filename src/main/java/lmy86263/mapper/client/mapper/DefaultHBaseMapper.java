package lmy86263.mapper.client.mapper;

import com.google.common.base.Preconditions;
import lmy86263.mapper.annotation.*;
import lmy86263.mapper.client.model.ColumnDefinition;
import lmy86263.mapper.client.model.TableDefinition;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lmy86263
 * @date 2018/08/16
 */
public class DefaultHBaseMapper extends AbstractMapper {
    @Getter
    private TableDefinition tableInfo;

    public DefaultHBaseMapper() {
    }

    @Override
    public boolean validateEntity(Class<?> clazz) {
        Preconditions.checkNotNull(clazz, "entity class cannot be null");
        Preconditions.checkNotNull(clazz.getAnnotation(Table.class),
                "annotation @Table cannot be null");

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Column.class) == null) {
                Preconditions.checkNotNull(field.getAnnotation(RowKey.class),
                        String.format("annotation @RowKey or @Column cannot be null for field %s", field.getName()));
            } else {
                Preconditions.checkNotNull(field.getAnnotation(ColumnFamily.class),
                        String.format("annotation @ColumnFamily cannot be null for field %s", field.getName()));
            }
        }
        return true;
    }

    @Override
    public void parseMappedEntity(Class<?> clazz) {
        Preconditions.checkArgument(validateEntity(clazz), "validate mapped entity failed");
        parseClassMetaData(clazz);
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.getAnnotation(RowKey.class) == null)
                .forEach(this::parseFieldMetaData);
    }

    void parseClassMetaData(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        String tableName = table.name();
        String namespace = table.namespace();
        Namespace ns = clazz.getAnnotation(Namespace.class);
        if (ns != null) {
            namespace = ns.name();
        }
        Field rowKeyField = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.getAnnotation(RowKey.class) != null).findAny().get();
        this.tableInfo = new TableDefinition.Builder(namespace, tableName)
                .rowkey(rowKeyField.getAnnotation(RowKey.class).name())
                .build();
    }

    void parseFieldMetaData(Field field) {
        String columnName = field.getDeclaredAnnotation(Column.class).name();
        String familyName = field.getDeclaredAnnotation(ColumnFamily.class).name();
        ColumnDefinition columnInfo = new ColumnDefinition.Builder(columnName, familyName).build();

        List<ColumnDefinition> columns = this.tableInfo.getFamilies().computeIfAbsent(familyName, k -> new LinkedList<>());
        columns.add(columnInfo);
    }
}
