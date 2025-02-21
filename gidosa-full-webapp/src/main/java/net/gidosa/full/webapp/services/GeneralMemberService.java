package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

@Log4j2
@Service
@RequiredArgsConstructor
public class GeneralMemberService {

    private final MemberGeneralJpaRepository memberGeneralJpaRepository;
//    private final ConstructionJpaRepository constructionJpaRepository;
    private final PasswordEncoder passwordEncoder;

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
} 