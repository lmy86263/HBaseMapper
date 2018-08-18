package lmy86263.mapper.client.mapper;

import lmy86263.mapper.client.model.TableDefinition;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author lmy86263
 * @date 2018/08/18
 */
@Getter
@Setter
public class HBaseMapperMetaData {

    private List<TableDefinition> tables;

}
