package lmy86263.mapper.exception;

/**
 * @author lmy86263
 * @date 2018/08/16
 */
public class HBaseMapperException extends RuntimeException {

    public HBaseMapperException() {
        super();
    }

    public HBaseMapperException(String message) {
        super(message);
    }

    public HBaseMapperException(String message, Throwable t) {
        super(message, t);
    }

}
