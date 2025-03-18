package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType3;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomMenuContentType3Repository extends JpaRepository<CustomMenuContentType3, Long> {
    Optional<CustomMenuContentType3> findByMenuId(Long menuId);
    void deleteByMenuId(Long menuId);
} 