package lmy86263.mapper;

import lmy86263.mapper.client.ConnectionManager;
import lmy86263.mapper.client.ConnectionWrapper;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Connection;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @author lmy86263
 * @date 2018/08/16
 */
public class ConnectionTester {

    @Test
    public void testConnection() throws IOException {
        Properties props = new Properties();
        props.setProperty(HConstants.ZOOKEEPER_QUORUM, "ubuntu");

        ConnectionManager manager = new ConnectionManager(props);
        ConnectionWrapper wrapper = manager.getConnectionWrapper();
        Connection conn = wrapper.getConn();
    }
}
