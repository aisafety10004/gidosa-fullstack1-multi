package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MasterDivisionDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("마스터 구분 상세 고유 식별자")
    private Integer id; // 마스터 구분 상세 고유 식별자

    @Column(nullable = false)
    @Comment("마스터 구분 상세 이름")
    private String name; // 마스터 구분 상세 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_authority_division_id")
    @Comment("마스터 권한 구분 정보")
    private MasterAuthorityDivision masterAuthorityDivision; // 마스터 권한 구분 정보
}
