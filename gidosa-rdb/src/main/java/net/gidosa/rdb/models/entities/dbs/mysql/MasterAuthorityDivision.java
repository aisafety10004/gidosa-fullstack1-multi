package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MasterAuthorityDivision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("마스터 권한 구분 고유 식별자")
    private Integer id; // 마스터 권한 구분 고유 식별자

    @Column(nullable = false)
    @Comment("마스터 권한 구분 이름")
    private String name; // 마스터 권한 구분 이름
}
