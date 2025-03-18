package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CustomMenuContentType2Repository extends JpaRepository<CustomMenuContentType2, Long> {
    
    // 특정 메뉴와 날짜의 타입2 컨텐츠 조회
    Optional<CustomMenuContentType2> findByMenuIdAndContentDate(Long menuId, LocalDate contentDate);
    
    // 특정 메뉴와 날짜의 타입2 컨텐츠 삭제
    void deleteByMenuIdAndContentDate(Long menuId, LocalDate contentDate);
} 