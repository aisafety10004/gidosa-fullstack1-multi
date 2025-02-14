package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import net.gidosa.full.webapp.models.dtos.MemberRegisterDto;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import net.gidosa.rdb.repositories.mysql.jpa.MemberGeneralJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberGeneralService {

    private final MemberGeneralJpaRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberGeneral register(MemberRegisterDto registerDto) {
        if (memberRepository.existsByUsername(registerDto.getUsername())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if (memberRepository.existsByEmail(registerDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        MemberGeneral member = new MemberGeneral();
        member.setUsername(registerDto.getUsername());
        member.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        member.setName(registerDto.getName());
        member.setEmail(registerDto.getEmail());

        return memberRepository.save(member);
    }
} 