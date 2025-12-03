package io.github.pangju666.framework.data.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.pangju666.framework.data.mybatisplus.entity.DocDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DocMapper extends BaseMapper<DocDO> {
}