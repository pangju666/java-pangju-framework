package io.github.pangju666.framework.data.mybatisplus.repository;

import io.github.pangju666.framework.data.mybatisplus.entity.DocDO;
import io.github.pangju666.framework.data.mybatisplus.mapper.DocMapper;
import org.springframework.stereotype.Repository;

@Repository
public class DocRepository extends BaseRepository<DocMapper, DocDO> {
}