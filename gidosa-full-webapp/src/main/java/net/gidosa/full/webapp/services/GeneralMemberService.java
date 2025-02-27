package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.common.utils.PasswordUtil;
import net.gidosa.full.webapp.dtos.MemberUpdateDto;
import net.gidosa.full.webapp.models.dtos.MemberRegisterDto;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.MemberGeneralJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Log4j2
@Service
@RequiredArgsConstructor
public class GeneralMemberService {
    private final MemberGeneralJpaRepository memberGeneralJpaRepository;
//    private final ConstructionJpaRepository constructionJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AdminMemberService adminMemberService;

    @Transactional
    public MemberGeneral register(MemberRegisterDto registerDto, Construction construction) {
        if (memberGeneralJpaRepository.existsByUsername(registerDto.getUsername())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if (memberGeneralJpaRepository.existsByEmail(registerDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }
//        Construction construction = constructionJpaRepository.findById(registerDto.getConstructionId()).orElse(null);
//        if (Objects.isNull(construction)) {
//            throw new IllegalArgumentException("해당 건물공사현장은 존재하지 않습니다.");
//        }

        MemberGeneral member = new MemberGeneral();
        member.setUsername(registerDto.getUsername());
        member.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        member.setName(registerDto.getName());
        member.setEmail(registerDto.getEmail());
        member.setPhone(registerDto.getPhone());
        member.setConstruction(construction);

        return memberGeneralJpaRepository.save(member);
    }

    public boolean isUsernameAvailable(String username) {
        return !memberGeneralJpaRepository.existsByUsername(username);
    }

    public Optional<MemberGeneral> findByNameAndEmail(String name, String email) {
        return memberGeneralJpaRepository.findByNameAndEmail(name, email);
    }

    public Optional<MemberGeneral> findByNameAndEmailAndConstructionId(String name, String email, Long constructionId) {
        return memberGeneralJpaRepository.findByNameAndEmailAndConstructionId(name, email, constructionId);
    }

    public boolean processFindPassword(String username, String email, Construction construction) {
        Optional<MemberGeneral> memberOpt = memberGeneralJpaRepository.findByUsernameAndEmail(username, email);
        
        if (memberOpt.isPresent()) {
            MemberGeneral member = memberOpt.get();
            String tempPassword = PasswordUtil.generateTempPassword();
            member.setPassword(passwordEncoder.encode(tempPassword));
            memberGeneralJpaRepository.save(member);

            String[] constructionManagerEmailCCList = adminMemberService.getAdminManagerMailList(construction.getId());
            emailService.sendTempPassword(email, username, tempPassword, constructionManagerEmailCCList);
            return true;
        }
        return false;
    }

    @Transactional
    public MemberGeneral updateGeneralMember(Long generalMemberId, MemberUpdateDto generalMemberUpdateDto) {
        if (Objects.isNull(generalMemberId)) {
            throw new IllegalArgumentException("회원 ID가 null입니다.");
        }
        if (Objects.isNull(generalMemberUpdateDto)) {
            throw new IllegalArgumentException("수정할 회원 정보가 null입니다.");
        }

        return memberGeneralJpaRepository.findById(generalMemberId)
            .map(member -> {
                // 필수 필드 유효성 검사
                if (generalMemberUpdateDto.getName() == null || generalMemberUpdateDto.getName().trim().isEmpty()) {
                    throw new IllegalArgumentException("이름은 필수 입력값입니다.");
                }
                if (generalMemberUpdateDto.getEmail() == null || generalMemberUpdateDto.getEmail().trim().isEmpty()) {
                    throw new IllegalArgumentException("이메일은 필수 입력값입니다.");
                }
                if (generalMemberUpdateDto.getPhone() == null || generalMemberUpdateDto.getPhone().trim().isEmpty()) {
                    throw new IllegalArgumentException("전화번호는 필수 입력값입니다.");
                }

                member.setName(generalMemberUpdateDto.getName().trim());
                member.setPhone(generalMemberUpdateDto.getPhone().trim());
                member.setEmail(generalMemberUpdateDto.getEmail().trim());
                
                if (generalMemberUpdateDto.getPassword() != null && !generalMemberUpdateDto.getPassword().trim().isEmpty()) {
                    member.setPassword(passwordEncoder.encode(generalMemberUpdateDto.getPassword().trim()));
                }
                
                return memberGeneralJpaRepository.save(member);
            })
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다. ID: " + generalMemberId));
    }

    public MemberGeneral getGeneralMemberById(Long generalMemberId) {
        return memberGeneralJpaRepository.findById(generalMemberId)
            .orElseThrow(() -> new RuntimeException("GeneralMember not found with id: " + generalMemberId));
    }

    public MemberGeneral getGeneralMemberByIdWithConstruction(Long generalMemberId) {
        return memberGeneralJpaRepository.findByIdWithConstruction(generalMemberId)
                .orElseThrow(() -> new RuntimeException("GeneralMember not found with id: " + generalMemberId));
    }

    public MemberGeneral getGeneralMemberByUsername(String username) {
        return memberGeneralJpaRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("GeneralMember not found with username: " + username));
    }

    public MemberGeneral getGeneralMemberByEmail(String email) {
        return memberGeneralJpaRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("GeneralMember not found with email: " + email));
    }

    public MemberGeneral getGeneralMemberByPhone(String phone) {
        return memberGeneralJpaRepository.findByPhone(phone)
            .orElseThrow(() -> new RuntimeException("GeneralMember not found with phone: " + phone));
    }

    public MemberGeneral getGeneralMemberByConstructionId(Long constructionId) {
        return memberGeneralJpaRepository.findByConstructionId(constructionId)
            .orElseThrow(() -> new RuntimeException("GeneralMember not found with constructionId: " + constructionId));
    }

    public MemberGeneral getGeneralMemberByConstructionIdAndUsername(Long constructionId, String username) {
        return memberGeneralJpaRepository.findByConstructionIdAndUsername(constructionId, username)
            .orElseThrow(() -> new RuntimeException("GeneralMember not found with constructionId: " + constructionId + " and username: " + username));
    }

    public MemberGeneral getGeneralMemberByConstructionIdAndEmail(Long constructionId, String email) {
        return memberGeneralJpaRepository.findByConstructionIdAndEmail(constructionId, email)
            .orElseThrow(() -> new RuntimeException("GeneralMember not found with constructionId: " + constructionId + " and email: " + email));
    }

    public MemberGeneral getGeneralMemberByConstructionIdAndPhone(Long constructionId, String phone) {
        return memberGeneralJpaRepository.findByConstructionIdAndPhone(constructionId, phone)
            .orElseThrow(() -> new RuntimeException("GeneralMember not found with constructionId: " + constructionId + " and phone: " + phone));
    }
}
