package lmy86263.mapper.client;

import lmy86263.mapper.exception.HBaseMapperException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lmy86263
 * @date 2018/08/15
 */
public class ConnectionManager extends PriorityQueue<ConnectionWrapper> {
    private Configuration config;
    private Properties props;

    private static final String HBASE = "hbase";
    private static final String ZOOKEEPER = "zookeeper";
    private static final String HADOOP = "hadoop";

    private int initSize = 5;
    private int incrementSize = 3;
    private int maxSize = 20;
    private int currentSize = 0;

    public ConnectionManager(Properties props) {
        super(Comparator.comparingInt(ConnectionWrapper::getLoad));

        this.initSize = Integer.parseInt(Optional.ofNullable(props.getProperty("")).orElseGet(() -> initSize + ""));
        this.incrementSize = Integer.parseInt(Optional.ofNullable(props.getProperty("")).orElseGet(() -> incrementSize + ""));
        this.maxSize = Integer.parseInt(Optional.ofNullable(props.getProperty("")).orElseGet(() -> maxSize + ""));
        this.props = props;
        Set<String> items = props.stringPropertyNames().stream().filter(
                item -> !item.startsWith("connection")).collect(Collectors.toSet());
        loadConfig(items);
        initPool();
    }

    void loadConfig(Set<String> items) {
        config = HBaseConfiguration.create();
        for (String key : items) {
            config.set(key, props.getProperty(key));
        }
    }
    
    public void initPool() {
        for (int i = 0; i < initSize; i++) {
            this.add(createConnection());
        }
        this.currentSize = initSize;
    }

    public ConnectionWrapper createConnection() {
        Connection conn;
        try {
            conn = ConnectionFactory.createConnection(config);
        } catch (IOException e) {
            throw new HBaseMapperException("create connection to HBase server failed");
        }
        return new ConnectionWrapper.Builder(conn, this).load(0).builder();
    }

    public void addConnection() {
        for (int i = 0; i < incrementSize; i++) {
            if (currentSize >= maxSize) {
                break;
            }
            this.add(createConnection());
            currentSize++;
        }
    }

    public ConnectionWrapper getConnectionWrapper() {
        if (this.isEmpty()) {
            addConnection();
        }
        ConnectionWrapper wrapper = this.poll();
        wrapper.setLoad(wrapper.getLoad() + 1);
        return wrapper;
    }

    public void close() {
        this.clear();
    }
}
