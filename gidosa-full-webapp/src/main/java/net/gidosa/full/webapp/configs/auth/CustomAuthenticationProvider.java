package net.gidosa.full.webapp.configs.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import net.gidosa.rdb.repositories.mysql.jpa.MemberGeneralJpaRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final MemberGeneralJpaRepository memberGeneralRepository;
//    private final PrincipalDetailsService principalDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        // WebAuthenticationDetails에서 constructionId 가져오기
        ConstructionAuthenticationDetails details = (ConstructionAuthenticationDetails) authentication.getDetails();
        Long constructionId = details.getConstructionId();
        
        // username과 constructionId로 사용자 정보 조회
//        PrincipalDetails principalDetails = (PrincipalDetails) principalDetailsService.loadUserByUsernameAndConstructionId(username, constructionId);
        Optional<MemberGeneral> optionalMemberGeneral = memberGeneralRepository.findByUsernameAndConstructionIdWithConstruction(username, constructionId);
        if (!optionalMemberGeneral.isPresent()) {
            throw new BadCredentialsException("해당 현장에 일치하는 회원정보가 없습니다.");
        }
        MemberGeneral memberGeneral = optionalMemberGeneral.get();
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(password, memberGeneral.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        
        // 사용자의 constructionId와 요청된 constructionId 비교
        Construction construction = memberGeneral.getConstruction();
        if (construction == null || !construction.getId().equals(constructionId)) {
            throw new BadCredentialsException("해당 현장에 접근 권한이 없습니다.");
        }

        // 인증 성공 시 새로운 인증 토큰 생성
        return new UsernamePasswordAuthenticationToken(
                getPrincipalDetails(optionalMemberGeneral),
//                username,
                password,
                Collections.singletonList(new SimpleGrantedAuthority(memberGeneral.getRole()))
        );
    }

    private PrincipalDetails getPrincipalDetails(Optional<MemberGeneral> optionalMemberGeneral) {
        MemberGeneral memberGeneral = optionalMemberGeneral.get();

        // 테스트용 임시 코드 (실제 구현 시 삭제 필요)
//        log.info("사용자 로그인 시도: username={}, constructionId={}", username, constructionId);
        PrincipalDetails principalDetails = new PrincipalDetails(
                memberGeneral.getUsername(),
                memberGeneral.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(memberGeneral.getRole())),
//                principalDetails.getAuthorities(),
                memberGeneral
        );
        return principalDetails;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
} 