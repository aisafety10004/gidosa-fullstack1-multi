package net.gidosa.full.webadmin.services;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.models.dtos.MemberAdminRegisterDto;
import net.gidosa.full.webadmin.models.dtos.MemberAdminUpdateDto;
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
import java.util.Objects;
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
        return memberAdminJpaRepository.findAllWithConstructionByOrderByIdDesc(pageable);
    }

    // ID로 관리자 조회
    public Optional<MemberAdmin> getMemberAdminById(Long id) {
        return memberAdminJpaRepository.findByIdWithConstruction(id);
    }

    // 사용자명으로 관리자 조회
    public Optional<MemberAdmin> getMemberAdminByUsername(String username) {
        return memberAdminJpaRepository.findByUsername(username);
    }

    // 이메일로 관리자 조회
    public Optional<MemberAdmin> getMemberAdminByEmail(String email) {
        return memberAdminJpaRepository.findByEmail(email);
    }

    // 관리자 정보 수정
    @Transactional
    public MemberAdmin updateMemberAdmin(MemberAdminUpdateDto memberAdminUpdateDto) {
        return memberAdminJpaRepository.findById(memberAdminUpdateDto.getId())
            .map(memberAdmin -> {
                memberAdmin.setName(memberAdminUpdateDto.getName());
                memberAdmin.setPhone(memberAdminUpdateDto.getPhone());
                memberAdmin.setEmail(memberAdminUpdateDto.getEmail());
                memberAdmin.setLocation(memberAdminUpdateDto.getLocation());
                if(!Objects.isNull(memberAdminUpdateDto.getConstructionId()))
                    constructionJpaRepository.findById(memberAdminUpdateDto.getConstructionId())
                            .ifPresent(memberAdmin::setConstruction);
                if (!Strings.isNullOrEmpty(memberAdminUpdateDto.getPassword())) {
                    memberAdmin.setPassword(passwordEncoder.encode(memberAdminUpdateDto.getPassword()));
                }
                return memberAdminJpaRepository.save(memberAdmin);
            })
            .orElseThrow(() -> new RuntimeException("Admin member not found with id: " + memberAdminUpdateDto.getId()));
    }

    // 관리자 삭제
    @Transactional
    public void deleteMemberAdmin(Long id) {
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
    public void toggleMemberAdminStatus(Long id) {
        memberAdminJpaRepository.findById(id)
            .ifPresent(memberAdmin -> {
                memberAdmin.setActive(!memberAdmin.isActive());
                memberAdminJpaRepository.save(memberAdmin);
            });
    }

    // 관리자 권한 변경
    @Transactional
    public void updateMemberAdminRole(Long id, String role) {
        memberAdminJpaRepository.findById(id)
            .ifPresent(member -> {
                member.setRole(role);
                memberAdminJpaRepository.save(member);
            });
    }

    // 관리자 등록 (DTO 사용)
    @Transactional
    public MemberAdmin registerMemberAdmin(MemberAdminRegisterDto dto) {
        MemberAdmin memberAdmin = new MemberAdmin();
        memberAdmin.setUsername(dto.getUsername());
        memberAdmin.setPassword(passwordEncoder.encode(dto.getPassword()));
        memberAdmin.setName(dto.getName());
        memberAdmin.setEmail(dto.getEmail());
        memberAdmin.setPhone(dto.getPhone());
        memberAdmin.setLocation(dto.getLocation());
        memberAdmin.setRole(dto.getRole());
        memberAdmin.setActive(true);

        // Construction 설정
        if (dto.getConstructionId() != null) {
            constructionJpaRepository.findById(dto.getConstructionId())
                .ifPresent(memberAdmin::setConstruction);
        }

        return memberAdminJpaRepository.save(memberAdmin);
    }

    // 지역별 관리자 조회
    public Page<MemberAdmin> getMemberAdminsByLocation(String location, Pageable pageable) {
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