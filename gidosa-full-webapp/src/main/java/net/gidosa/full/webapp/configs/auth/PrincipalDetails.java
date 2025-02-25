package net.gidosa.full.webapp.configs.auth;

import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class PrincipalDetails implements UserDetails {
    private static final long serialVersionUID = 1L;
    private MemberGeneral memberGeneral;

    // 일반 시큐리티 로그인시 사용
    public PrincipalDetails(MemberGeneral memberGeneral) {
        this.memberGeneral = memberGeneral;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return memberGeneral.getPassword();
    }

    @Override
    public String getUsername() {
        return memberGeneral.getUsername();
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

    public MemberGeneral getMemberGeneral() {
        return this.memberGeneral;
    }
    public void setMemberGeneral(MemberGeneral memberGeneral) {
        this.memberGeneral = memberGeneral;
    }
}
