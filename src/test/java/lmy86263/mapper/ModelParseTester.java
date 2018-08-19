package lmy86263.mapper;

import lmy86263.mapper.client.mapper.DefaultHBaseMapper;
import lmy86263.mapper.client.mapper.ModelMapper;
import org.junit.Test;
import org.reflections.Reflections;

import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lmy86263
 * @date 2018/08/17
 */
public class ModelParseTester {

    @Test
    public void testParseEntity() {
        // pass
        ModelMapper mapper = new DefaultHBaseMapper();
        mapper.parseMappedEntity(Person.class);
    }

    @Test
    public void testScanPackages() {
        // pass
        Reflections reflector = new Reflections("lmy86263.mapper");
        Set<Class<?>> mappedClasses = new HashSet<>();
        mappedClasses.addAll(reflector.getTypesAnnotatedWith(Target.class));
    }
}
