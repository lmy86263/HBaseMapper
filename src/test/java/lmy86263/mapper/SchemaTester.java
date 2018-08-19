package lmy86263.mapper;

import lmy86263.mapper.client.HBaseBootstrap;
import org.junit.Before;
import org.junit.Test;

/**
 * @author lmy86263
 * @date 2018/08/16
 */
public class SchemaTester {
    private HBaseBootstrap bootstrap;

    @Before
    public void init() {
        bootstrap = new HBaseBootstrap();
    }


    @Test
    public void testMapperSchema() {
        bootstrap.start();
    }
}
