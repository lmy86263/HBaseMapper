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
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;

import java.io.IOException;

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
        createInterval();
    }


    @Override
    @MapperSchema(name = Schemas.UPDATE)
    public void updateSchema() {
        metaData.getTables().forEach(this::updateTable);
    }


    @Override
    @MapperSchema(name = Schemas.CREATE_DROP)
    public void createOrDropSchema() {
        createInterval();
        Runtime.getRuntime().addShutdownHook(new Thread(this::deleteAllTables));
    }

    void createInterval() {
        this.deleteAllTables();
        metaData.getTables().forEach(this::createSchemaTableV1);
    }

    public void deleteAllTables() {
        TableName[] tableNames;
        try {
            tableNames = admin.listTableNames();
            for (TableName name : tableNames) {
                admin.disableTable(name);
                admin.deleteTable(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void createSchemaTable(TableDefinition table) {
//        TableDescriptorBuilder tableBuilder = TableDescriptorBuilder
//                .newBuilder(TableName.valueOf(table.getNameSpace(), table.getTableName()));
//        table.getFamilies().keySet().forEach(family -> tableBuilder
//                .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(family)).build()));
//        try {
//            admin.createTable(tableBuilder.build());
//        } catch (IOException e) {
//            throw new HBaseMapperException(String.format("create table %s:%s failed", table.getNameSpace(), table.getTableName()), e);
//        }
//    }

    public void createSchemaTableV1(TableDefinition table) {
        HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(table.getNameSpace(), table.getTableName()));
        table.getFamilies().keySet().forEach(family -> tableDescriptor.addFamily(new HColumnDescriptor(family)));
        try {
            admin.createTable(tableDescriptor);
        } catch (IOException e) {
            throw new HBaseMapperException(String.format("create table %s:%s failed", table.getNameSpace(), table.getTableName()), e);
        }
    }

    public void updateTable(TableDefinition table) {
        try {
            if (!admin.tableExists(TableName.valueOf(table.getNameSpace(), table.getTableName()))) {
                if (SystemParams.V2.equals(System.getProperty(SystemParams.HBASE_SERVER_VERSION))) {
//                    createSchemaTable(table);
                } else {
                    createSchemaTableV1(table);
                }
            }
        } catch (Exception e) {
            throw new HBaseMapperException(String.format("update table schema %s:%s failed", table.getNameSpace(), table.getTableName()), e);
        }
    }


}
