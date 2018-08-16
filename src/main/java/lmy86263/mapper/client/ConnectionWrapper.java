package lmy86263.mapper.client;

import lombok.Getter;
import lombok.Setter;
import org.apache.hadoop.hbase.client.Connection;

/**
 * @author lmy86263
 * @date 2018/08/15
 */
@Getter
public class ConnectionWrapper {
    private final Connection conn;
    private final ConnectionManager container;
    @Setter
    private int load;

    private ConnectionWrapper(Builder builder) {
        this.conn = builder.conn;
        this.load = builder.load;
        this.container = builder.container;
    }

    public static class Builder {
        private final Connection conn;
        private final ConnectionManager container;
        private int load = 0;

        public Builder(Connection conn, ConnectionManager container) {
            this.conn = conn;
            this.container = container;
        }

        public Builder load(int load) {
            this.load = load;
            return this;
        }

        public ConnectionWrapper builder() {
            return new ConnectionWrapper(this);
        }

    }

    public void close() {
        container.add(this);
    }
}
