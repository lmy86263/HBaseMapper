package lmy86263.mapper.schema;

import lmy86263.mapper.client.ConnectionWrapper;
import lmy86263.mapper.client.mapper.HBaseMapperMetaData;
import lmy86263.mapper.client.model.TableDefinition;
import lmy86263.mapper.configure.SystemParams;
import lmy86263.mapper.exception.HBaseMapperException;
import lmy86263.mapper.schema.common.AbstractSchemaManager;
import lmy86263.mapper.schema.common.MapperSchema;
import lmy86263.mapper.schema.common.Schemas;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lmy86263
 * @date 2018/08/15
 */
public class HBaseSchemaManager extends AbstractSchemaManager {

    private Admin admin;
    private HBaseMapperMetaData metaData;

    public HBaseSchemaManager(ConnectionWrapper wrapper, HBaseMapperMetaData metaData) {
        try {
            this.admin = wrapper.getConn().getAdmin();
        } catch (IOException e) {
            throw new HBaseMapperException("get HBase admin in SchemaManager failed", e);
        }
        this.metaData = metaData;
    }


    @Override
    @MapperSchema(name = Schemas.CREATE)
    public void createSchema() {
        metaData.getTables().forEach(table -> {
            deleteTableIfPresent(table);
            createOrOverrideTable(table);
        });
    }

    @Override
    @MapperSchema(name = Schemas.UPDATE)
    public void updateSchema() {
        metaData.getTables().forEach(this::createOrOverrideTable);
    }

    @Override
    @MapperSchema(name = Schemas.CREATE_DROP)
    public void createOrDropSchema() {
        createSchema();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> metaData.getTables().forEach(this::deleteTableIfPresent)));
    }

    public void createOrOverrideTable(TableDefinition table) {
        if (SystemParams.V2.equals(System.getProperty(SystemParams.HBASE_SERVER_VERSION))) {
//            createSchemaTable(table);
        } else {
            createSchemaTableV1(table);
        }
    }

//    public void createSchemaTable(TableDefinition table) {
//        createNamespaceIfAbsent(table.getNameSpace());
//        TableName tableName = TableName.valueOf(table.getNameSpace(), table.getTableName());
//        try {
//            // if the client version is 2.x, and the server is 1.x, the method tableExist throws NoSuchColumnFamilyException
//            if (!admin.tableExists(tableName)) {
//                TableDescriptorBuilder tableBuilder = TableDescriptorBuilder
//                        .newBuilder(TableName.valueOf(table.getNameSpace(), table.getTableName()));
//                table.getFamilies().keySet().forEach(family -> tableBuilder
//                        .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(family)).build()));
//                admin.createTable(tableBuilder.build());
//            } else {
//                TableDescriptor td = admin.getDescriptor(tableName);
//                TableDescriptorBuilder tdb = TableDescriptorBuilder.newBuilder(td);
//                Set<String> mappedCfs = table.getFamilies().keySet();
//                Set<String> existCfs = Arrays.stream(td.getColumnFamilies()).map(ColumnFamilyDescriptor::getNameAsString).collect(Collectors.toSet());
//                Set<String> nonExistCfs = mappedCfs.stream().filter(mappedCf -> !existCfs.contains(mappedCf)).collect(Collectors.toSet());
//                nonExistCfs.forEach(nonExistCf -> tdb.setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(nonExistCf)).build()));
//
//                if (!nonExistCfs.isEmpty()) {
//                    admin.disableTable(tableName);
//                    admin.modifyTable(tdb.build());
//                    admin.enableTable(tableName);
//                }
//            }
//        } catch (Exception e) {
//            throw new HBaseMapperException(String.format("create table %s:%s failed", table.getNameSpace(), table.getTableName()), e);
//        }
//    }

    public void createSchemaTableV1(TableDefinition table) {
        createNamespaceIfAbsent(table.getNameSpace());
        TableName tableName = TableName.valueOf(table.getNameSpace(), table.getTableName());
        try {
            if (!admin.tableExists(tableName)) {
                HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(table.getNameSpace(), table.getTableName()));
                table.getFamilies().keySet().forEach(family -> tableDescriptor.addFamily(new HColumnDescriptor(family)));
                admin.createTable(tableDescriptor);
            } else {
                HTableDescriptor htd = admin.getTableDescriptor(tableName);
                Set<String> mappedCfs = table.getFamilies().keySet();
                Set<String> existCfs = Arrays.stream(htd.getColumnFamilies()).map(HColumnDescriptor::getNameAsString).collect(Collectors.toSet());
                Set<String> nonExistCfs = mappedCfs.stream().filter(mappedCf -> !existCfs.contains(mappedCf)).collect(Collectors.toSet());
                nonExistCfs.forEach(nonExistCf -> htd.addFamily(new HColumnDescriptor(Bytes.toBytes(nonExistCf))));

                if (!nonExistCfs.isEmpty()) {
                    admin.disableTable(tableName);
                    admin.modifyTable(tableName, htd);
                    admin.enableTable(tableName);
                }
            }
        } catch (Exception e) {
            throw new HBaseMapperException(String.format("create table %s:%s failed", table.getNameSpace(), table.getTableName()), e);
        }
    }

    public void createNamespaceIfAbsent(String namespace) {
        Set<String> namespaces = null;
        try {
            namespaces = Arrays.stream(admin.listNamespaceDescriptors()).map(nsp -> nsp.getName()).collect(Collectors.toSet());
            if (!namespaces.contains(namespace)) {
                admin.createNamespace(NamespaceDescriptor.create(namespace).build());
            }
        } catch (Exception e) {
            throw new HBaseMapperException(String.format("create namespace %s failed", namespace), e);
        }


    }

    public void deleteTableIfPresent(TableDefinition table) {
        try {
            TableName tableName = TableName.valueOf(table.getNameSpace(), table.getTableName());
            if (admin.tableExists(tableName)) {
                admin.disableTable(tableName);
                admin.deleteTable(tableName);
            }
        } catch (Exception e) {
            throw new HBaseMapperException(String.format("delete table %s:%s failed", table.getNameSpace(), table.getTableName()), e);
        }
    }
}
