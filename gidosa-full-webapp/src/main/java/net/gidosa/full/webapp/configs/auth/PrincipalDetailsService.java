//package net.gidosa.full.webapp.configs.auth;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
//import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
//import net.gidosa.rdb.repositories.mysql.jpa.MemberAdminJpaRepository;
//import net.gidosa.rdb.repositories.mysql.jpa.MemberGeneralJpaRepository;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.Optional;
//
//@Log4j2
//@Service
//@RequiredArgsConstructor
//public class PrincipalDetailsService implements UserDetailsService {
//    private final MemberGeneralJpaRepository memberGeneralRepository;
//
////    @Override
////    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
////        MemberGeneral memberGeneral = memberGeneralRepository.findByUsernameWithConstruction(username)
////                .orElseThrow(() -> new UsernameNotFoundException("MemberGeneral User not found with username: " + username));
////
////        return new PrincipalDetails(memberGeneral);
//////        return new User(memberAdmin.getUsername(),
//////                memberAdmin.getPassword(),
//////                Collections.singleton(new SimpleGrantedAuthority(memberGeneral.getRole())));
////    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // 일반적인 username만으로는 로그인하지 않도록 설정
//        throw new UsernameNotFoundException("constructionId가 필요합니다.");
//    }
//
//    public UserDetails loadUserByUsernameAndConstructionId(String username, Long constructionId)
//            throws UsernameNotFoundException {
//        if (username == null || constructionId == null) {
//            throw new UsernameNotFoundException("아이디 또는 현장 정보가 제공되지 않았습니다.");
//        }
//
//        // 실제 사용자 조회 로직 구현
//        Optional<MemberGeneral> optionalMemberGeneral =  memberGeneralRepository.findByUsernameAndConstructionIdWithConstruction(username, constructionId);
//        if(!optionalMemberGeneral.isPresent()) {
//            throw new UsernameNotFoundException("해당 아이디와 현장 정보로 사용자를 찾을 수 없습니다.");
//        }
//
//        return getPrincipalDetails(optionalMemberGeneral);
//    }
//
//    private PrincipalDetails getPrincipalDetails(Optional<MemberGeneral> optionalMemberGeneral) {
//        MemberGeneral memberGeneral = optionalMemberGeneral.get();
//
//        // 테스트용 임시 코드 (실제 구현 시 삭제 필요)
////        log.info("사용자 로그인 시도: username={}, constructionId={}", username, constructionId);
//        PrincipalDetails principalDetails = new PrincipalDetails(
//                memberGeneral.getUsername(),
//                memberGeneral.getPassword(),
//                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
////                principalDetails.getAuthorities(),
//                memberGeneral
//        );
//        return principalDetails;
//    }
//}
