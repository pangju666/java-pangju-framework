package io.github.pangju666.framework.data.mybatisplus.type.handler;

import io.github.pangju666.framework.data.mybatisplus.type.handler.base.GenericsVarcharListTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class StringVarcharListTypeHandler extends GenericsVarcharListTypeHandler<String> {
    public StringVarcharListTypeHandler() {
        super((value) -> value);
    }
}
