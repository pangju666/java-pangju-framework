package io.github.pangju666.framework.data.mybatisplus.repository;

import io.github.pangju666.framework.data.mybatisplus.entity.TestDO;
import io.github.pangju666.framework.data.mybatisplus.mapper.TestMapper;
import org.springframework.stereotype.Repository;

@Repository
public class TestRepository extends BaseRepository<TestMapper, TestDO> {
}