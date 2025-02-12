package net.gidosa.full.webadmin.mappers;

import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface AuthMapper {
    LocalDateTime test1();
}
