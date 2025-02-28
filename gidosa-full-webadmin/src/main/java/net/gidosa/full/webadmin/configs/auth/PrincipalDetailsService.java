package net.gidosa.full.webadmin.configs.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import net.gidosa.rdb.repositories.mysql.jpa.MemberAdminJpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final MemberAdminJpaRepository memberAdminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberAdmin memberAdmin = memberAdminRepository.findByUsernameWithConstructionAndManagementMenus(username)
                .orElseThrow(() -> new UsernameNotFoundException("MemberAdmin User not found with username: " + username));
        
        return new PrincipalDetails(memberAdmin);
//        return new User(memberAdmin.getUsername(),
//                memberAdmin.getPassword(),
//                Collections.singleton(new SimpleGrantedAuthority(memberAdmin.getRole())));
    }
} 