package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class RequestConstruction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("공사 요청 고유 식별자")
    private Long id; // 공사 요청 고유 식별자

    @Column(nullable = false)
    @Comment("요청자 이름")
    private String name; // 요청자 이름
    
    @Column(nullable = false)
    @Comment("요청자 전화번호")
    private String phone; // 요청자 전화번호
    
    @Column(nullable = false)
    @Comment("공사 위치")
    private String constructionLocation; // 공사 위치
    
    @Column(nullable = false)
    @Comment("요청 상태")
    private String position; // 요청 상태
    
    @Column(length = 1000)
    @Comment("요청 메시지")
    private String message; // 요청 메시지
    
    @Column(nullable = false)
    @Comment("동의 여부")
    private boolean agreement; // 동의 여부
    
    @CreatedDate
    @Comment("생성 일시")
    @CreationTimestamp
    private LocalDateTime createdAt; // 생성 일시
    
    @UpdateTimestamp
    @Comment("수정 일시")
    private LocalDateTime updatedAt; // 수정 일시
}
