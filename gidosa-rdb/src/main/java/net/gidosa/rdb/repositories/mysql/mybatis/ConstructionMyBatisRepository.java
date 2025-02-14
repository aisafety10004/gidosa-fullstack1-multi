package net.gidosa.rdb.repositories.mysql.mybatis;

import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ConstructionMyBatisRepository {
    List<Construction> findByLocationContaining(String keyword);
}
