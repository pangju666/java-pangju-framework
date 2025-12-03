package io.github.pangju666.framework.data.mybatisplus.repository;

import io.github.pangju666.framework.data.mybatisplus.entity.UserDO;
import io.github.pangju666.framework.data.mybatisplus.mapper.UserMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends BaseRepository<UserMapper, UserDO> {
}