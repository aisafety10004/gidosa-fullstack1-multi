package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import net.gidosa.rdb.repositories.mysql.jpa.MemberGeneralJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberGeneralService {
    private final MemberGeneralJpaRepository memberGeneralJpaRepository;
    private final PasswordEncoder passwordEncoder;

    // 전체 회원 조회
    public Page<MemberGeneral> getAllMembersWithPaging(Pageable pageable) {
        return memberGeneralJpaRepository.findAllByOrderByIdDesc(pageable);
    }

    // ID로 회원 조회
    public Optional<MemberGeneral> getMemberById(Long id) {
        return memberGeneralJpaRepository.findById(id);
    }

    // 사용자명으로 회원 조회
    public Optional<MemberGeneral> getMemberByUsername(String username) {
        return memberGeneralJpaRepository.findByUsername(username);
    }

    // 이메일로 회원 조회
    public Optional<MemberGeneral> getMemberByEmail(String email) {
        return memberGeneralJpaRepository.findByEmail(email);
    }

    // 회원 정보 수정
    @Transactional
    public MemberGeneral updateMember(Long id, MemberGeneral memberDetails) {
        return memberGeneralJpaRepository.findById(id)
            .map(member -> {
                // 수정 불가능한 필드는 제외하고 업데이트
                member.setName(memberDetails.getName());
                member.setPhone(memberDetails.getPhone());
                member.setEmail(memberDetails.getEmail());
                // 비밀번호는 별도의 암호화 처리가 필요할 수 있음
                if (memberDetails.getPassword() != null && !memberDetails.getPassword().isEmpty()) {
                    member.setPassword(passwordEncoder.encode(memberDetails.getPassword()));
                }
                return memberGeneralJpaRepository.save(member);
            })
            .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
    }

    // 회원 삭제
    @Transactional
    public void deleteMember(Long id) {
        memberGeneralJpaRepository.deleteById(id);
    }

    // 회원 존재 여부 확인 (사용자명)
    public boolean existsByUsername(String username) {
        return memberGeneralJpaRepository.existsByUsername(username);
    }

    // 회원 존재 여부 확인 (이메일)
    public boolean existsByEmail(String email) {
        return memberGeneralJpaRepository.existsByEmail(email);
    }

    public Page<MemberGeneral> getMembersByConstructionId(Long constructionId, Pageable pageable) {
        return memberGeneralJpaRepository.findByConstructionId(constructionId, pageable);
    }
}
