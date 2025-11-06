package io.github.pangju666.framework.data.mybatisplus.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import io.github.pangju666.framework.data.mybatisplus.entity.TestDO
import org.apache.ibatis.annotations.Mapper

@Mapper
interface TestMapper extends BaseMapper<TestDO> {
}