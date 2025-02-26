package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.Notice;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticeJpaRepository extends JpaRepository<Notice, Long> {
    // 기본적인 CRUD 작업은 JpaRepository에서 제공
    
    // ID 기준 내림차순 정렬된 페이징 처리된 공지사항 목록 조회
    Page<Notice> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT n FROM Notice n " +
           "LEFT JOIN FETCH n.fileAttachment1 " +
           "LEFT JOIN FETCH n.fileAttachment2 " +
           "LEFT JOIN FETCH n.fileAttachment3 " +
           "LEFT JOIN FETCH n.fileAttachment4 " +
           "LEFT JOIN FETCH n.fileAttachment5 " +
           "WHERE n.id = :id")
    Optional<Notice> findByIdWithAttachments(@Param("id") Long id);

} 