package lmy86263.mapper.configure;

/**
 * @author lmy86263
 * @date 2018/08/15
 */
public final class SystemParams {

    public static final String HADOOP_HOME = "hadoop.home.dir";

    public static final String OS_NAME = "os.name";

    public static final String HBASE_MAPPER_SCHEMA = "hbase.mapper.schema";

    public enum OS {
        /**
         * windows OS
         */
        WINDOWS("Windows"),
        LINUX("Linux"),
        MAC_OS("Mac OS");

        private String name;

        OS(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
