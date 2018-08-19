package lmy86263.mapper.client.model;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lmy86263
 * @date 2018/08/16
 */
@Getter
public class TableDefinition {

    private final String nameSpace;
    private final String tableName;
    private final String rowKey;
    private Map<String, List<ColumnDefinition>> families;

    private TableDefinition(Builder builder) {
        this.nameSpace = builder.nameSpace;
        this.tableName = builder.tableName;
        this.rowKey = builder.rowKey;
        families = new ConcurrentHashMap<>();
    }


    public static class Builder {
        private final String nameSpace;
        private final String tableName;
        private String rowKey;

        public Builder(String nameSpace, String tableName) {
            this.nameSpace = nameSpace;
            this.tableName = tableName;
        }

        public Builder rowkey(String rowKey) {
            this.rowKey = rowKey;
            return this;
        }


        public TableDefinition build() {
            return new TableDefinition(this);
        }
    }

}
