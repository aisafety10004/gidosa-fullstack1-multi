package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import net.gidosa.rdb.repositories.mysql.jpa.MemberAdminJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class AdminMemberService {
    private final MemberAdminJpaRepository memberAdminJpaRepository;

    /**
     * 특정 건설 현장의 관리자 이메일 목록을 가져옵니다.
     * @param constructionId 건설 현장 ID
     * @return 관리자 이메일 배열
     */
    public String[] getAdminManagerMailList(Long constructionId) {
        List<MemberAdmin> managers = memberAdminJpaRepository.findByConstructionIdAndIsActiveTrue(constructionId);
        return managers.stream()
                .map(MemberAdmin::getEmail)
                .toArray(String[]::new);
    }
}
