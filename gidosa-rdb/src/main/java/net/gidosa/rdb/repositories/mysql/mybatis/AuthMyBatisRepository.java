package net.gidosa.rdb.repositories.mysql.mybatis;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface AuthMyBatisRepository {
    LocalDateTime test1();
}

