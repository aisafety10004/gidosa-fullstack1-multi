package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomMenuContentType1Repository extends JpaRepository<CustomMenuContentType1, Long> {
    
    // 특정 메뉴의 타입1 컨텐츠 조회
    Optional<CustomMenuContentType1> findByMenuId(Long menuId);
} 