package net.gidosa.full.webadmin.configs.auth;

import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PrincipalDetails implements UserDetails, OAuth2User, Serializable {
    private static final long serialVersionUID = 1L;
    
    // 엔티티 대신 필요한 정보만 저장
    // private Long id;
    // private String username;
    // private String password;
    // private String role;
    // private String name;
    // // 기타 필요한 필드...
    
    // // 직렬화에서 제외
    // @Transient
    // private transient MemberAdmin memberAdmin;
    private MemberAdmin memberAdmin;
    
    private Map<String, Object> attributes;

    // 일반 시큐리티 로그인시 사용
    public PrincipalDetails(MemberAdmin memberAdmin) {
        this.memberAdmin = memberAdmin;
    }

    // OAuth2.0 로그인시 사용
    public PrincipalDetails(MemberAdmin memberAdmin, Map<String, Object> attributes) {
        this.memberAdmin = memberAdmin;
        this.attributes = attributes;
    }

    public MemberAdmin getMemberAdmin() {
        return memberAdmin;
    }

    @Override
    public String getPassword() {
        return memberAdmin.getPassword();
    }

    @Override
    public String getUsername() {
        return memberAdmin.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collet = new ArrayList<>();
        collet.add(()->{ return memberAdmin.getRole();});
        return collet;
    }

    // 리소스 서버로 부터 받는 회원정보(OAuth2)
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // User의 PrimaryKey
    @Override
    public String getName() {
        return memberAdmin.getId()+"";
    }
}
