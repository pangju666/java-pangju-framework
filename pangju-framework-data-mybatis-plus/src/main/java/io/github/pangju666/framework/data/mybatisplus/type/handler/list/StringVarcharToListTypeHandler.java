package io.github.pangju666.framework.data.mybatisplus.type.handler.list;

import io.github.pangju666.framework.data.mybatisplus.type.handler.base.GenericsVarcharToListTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

@MappedTypes({List.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class StringVarcharToListTypeHandler extends GenericsVarcharToListTypeHandler<String> {
	public StringVarcharToListTypeHandler() {
		super((value) -> value);
	}
}
