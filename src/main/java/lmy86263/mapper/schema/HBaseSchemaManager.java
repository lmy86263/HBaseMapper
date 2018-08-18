package lmy86263.mapper.schema;

import lmy86263.mapper.client.ConnectionWrapper;
import lmy86263.mapper.client.mapper.HBaseMapperMetaData;
import lmy86263.mapper.client.model.TableDefinition;
import lmy86263.mapper.exception.HBaseMapperException;
import lmy86263.mapper.schema.common.AbstractSchemaManager;
import lmy86263.mapper.schema.common.MapperSchema;
import lmy86263.mapper.schema.common.Schemas;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.util.Bytes;

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
        this.deleteAllTables();
        metaData.getTables().forEach(this::createSchemaTable);
    }


    @Override
    @MapperSchema(name = Schemas.UPDATE)
    public void updateSchema() {
        metaData.getTables().forEach(this::updateTable);
    }


    @Override
    @MapperSchema(name = Schemas.CREATE_DROP)
    public void createOrDropSchema() {
        this.deleteAllTables();
        metaData.getTables().forEach(this::createSchemaTable);
        Runtime.getRuntime().addShutdownHook(new Thread(this::deleteAllTables));
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

    public void createSchemaTable(TableDefinition table) {
        TableDescriptorBuilder tableBuilder = TableDescriptorBuilder
                .newBuilder(TableName.valueOf(table.getNameSpace(), table.getTableName()));
        table.getFamilies().keySet().forEach(family -> tableBuilder
                .setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(family)).build()));
        try {
            admin.createTable(tableBuilder.build());
        } catch (IOException e) {
            throw new HBaseMapperException(String.format("create table %s:%s failed", table.getNameSpace(), table.getTableName()), e);
        }
    }

    public void updateTable(TableDefinition table) {
        try {
            if (!admin.tableExists(TableName.valueOf(table.getNameSpace(), table.getTableName()))) {
                createSchemaTable(table);
            }
        } catch (IOException e) {
            throw new HBaseMapperException(String.format("decide table %s:%s exists failed", table.getNameSpace(), table.getTableName()), e);
        }
    }


}
