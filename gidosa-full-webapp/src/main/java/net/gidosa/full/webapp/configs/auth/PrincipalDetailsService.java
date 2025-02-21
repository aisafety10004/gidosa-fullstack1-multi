package net.gidosa.full.webapp.configs.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import net.gidosa.rdb.repositories.mysql.jpa.MemberAdminJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.MemberGeneralJpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final MemberGeneralJpaRepository memberGeneralRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberGeneral memberGeneral = memberGeneralRepository.findByUsernameWithConstruction(username)
                .orElseThrow(() -> new UsernameNotFoundException("MemberGeneral User not found with username: " + username));

        return new PrincipalDetails(memberGeneral);
//        return new User(memberAdmin.getUsername(),
//                memberAdmin.getPassword(),
//                Collections.singleton(new SimpleGrantedAuthority(memberGeneral.getRole())));
    }
}
