package io.github.pangju666.framework.data.mybatisplus.repository;

import io.github.pangju666.framework.data.mybatisplus.entity.MySQLTestEntity;
import io.github.pangju666.framework.data.mybatisplus.mapper.MySQLTestMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MySQLTestRepository extends BaseRepository<MySQLTestMapper, MySQLTestEntity> {
}