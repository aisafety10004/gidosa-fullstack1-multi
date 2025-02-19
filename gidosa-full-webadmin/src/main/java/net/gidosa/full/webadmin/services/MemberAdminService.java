package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import net.gidosa.rdb.repositories.mysql.jpa.MemberAdminJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
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
public class MemberAdminService {
    private final MemberAdminJpaRepository memberAdminJpaRepository;
    private final ConstructionJpaRepository constructionJpaRepository;
    private final PasswordEncoder passwordEncoder;

    // 전체 관리자 조회 (페이징)
    public Page<MemberAdmin> getAllMembersWithPaging(Pageable pageable) {
        return memberAdminJpaRepository.findAllByOrderByIdDesc(pageable);
    }

    // ID로 관리자 조회
    public Optional<MemberAdmin> getMemberById(Long id) {
        return memberAdminJpaRepository.findById(id);
    }

    // 사용자명으로 관리자 조회
    public Optional<MemberAdmin> getMemberByUsername(String username) {
        return memberAdminJpaRepository.findByUsername(username);
    }

    // 이메일로 관리자 조회
    public Optional<MemberAdmin> getMemberByEmail(String email) {
        return memberAdminJpaRepository.findByEmail(email);
    }

    // 관리자 정보 수정
    @Transactional
    public MemberAdmin updateMember(Long id, MemberAdmin memberDetails) {
        return memberAdminJpaRepository.findById(id)
            .map(memberAdmin -> {
                memberAdmin.setName(memberDetails.getName());
                memberAdmin.setPhone(memberDetails.getPhone());
                memberAdmin.setEmail(memberDetails.getEmail());
                memberAdmin.setLocation(memberDetails.getLocation());
                memberAdmin.setConstruction(memberDetails.getConstruction());
                if (memberDetails.getPassword() != null && !memberDetails.getPassword().isEmpty()) {
                    memberAdmin.setPassword(passwordEncoder.encode(memberDetails.getPassword()));
                }
                return memberAdminJpaRepository.save(memberAdmin);
            })
            .orElseThrow(() -> new RuntimeException("Admin member not found with id: " + id));
    }

    // 관리자 삭제
    @Transactional
    public void deleteMember(Long id) {
        memberAdminJpaRepository.deleteById(id);
    }

    // 관리자 존재 여부 확인 (사용자명)
    public boolean existsByUsername(String username) {
        return memberAdminJpaRepository.existsByUsername(username);
    }

    // 관리자 존재 여부 확인 (이메일)
    public boolean existsByEmail(String email) {
        return memberAdminJpaRepository.existsByEmail(email);
    }

    // 관리자 계정 활성/비활성 토글
    @Transactional
    public void toggleMemberStatus(Long id) {
        memberAdminJpaRepository.findById(id)
            .ifPresent(memberAdmin -> {
                memberAdmin.setActive(!memberAdmin.isActive());
                memberAdminJpaRepository.save(memberAdmin);
            });
    }

    // 관리자 권한 변경
    @Transactional
    public void updateMemberRole(Long id, String role) {
        memberAdminJpaRepository.findById(id)
            .ifPresent(member -> {
                member.setRole(role);
                memberAdminJpaRepository.save(member);
            });
    }

    // 관리자 등록
    @Transactional
    public MemberAdmin registerMember(MemberAdmin memberAdmin) {
        // 기본값 설정
        memberAdmin.setActive(true);
        if (memberAdmin.getRole() == null) {
            memberAdmin.setRole("ROLE_MANAGER"); // 기본 권한 설정
        }
        // 비밀번호 암호화 처리가 필요한 경우 여기서 수행
        memberAdmin.setPassword(passwordEncoder.encode(memberAdmin.getPassword()));

        return memberAdminJpaRepository.save(memberAdmin);
    }

    // 지역별 관리자 조회
    public Page<MemberAdmin> getMembersByLocation(String location, Pageable pageable) {
        return memberAdminJpaRepository.findByLocation(location, pageable);
    }

    // 지역 변경
    @Transactional
    public void updateLocation(Long id, String location) {
        memberAdminJpaRepository.findById(id)
            .ifPresent(member -> {
                member.setLocation(location);
                memberAdminJpaRepository.save(member);
            });
    }

    // Construction 목록 조회 메서드 추가
    public List<Construction> getAllConstructions() {
        return constructionJpaRepository.findAll();
    }
} 