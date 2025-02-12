package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import net.gidosa.full.webapp.dto.MemberRegisterDto;
import net.gidosa.full.webapp.entity.Member;
import net.gidosa.full.webapp.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member register(MemberRegisterDto registerDto) {
        if (memberRepository.existsByUsername(registerDto.getUsername())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if (memberRepository.existsByEmail(registerDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        Member member = new Member();
        member.setUsername(registerDto.getUsername());
        member.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        member.setName(registerDto.getName());
        member.setEmail(registerDto.getEmail());

        return memberRepository.save(member);
    }
} 