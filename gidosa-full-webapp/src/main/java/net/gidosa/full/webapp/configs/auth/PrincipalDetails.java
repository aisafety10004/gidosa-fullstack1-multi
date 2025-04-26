package net.gidosa.full.webapp.configs.auth;

import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PrincipalDetails extends User implements UserDetails {
    private static final long serialVersionUID = 1L;
    private MemberGeneral memberGeneral;

    // 일반 시큐리티 로그인시 사용
    public PrincipalDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, MemberGeneral memberGeneral) {
        super(username, password, authorities);
        this.memberGeneral = memberGeneral;
    }

    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(()->{ return memberGeneral.getRole(); });
        return collection;
//        return Collections.singletonList(new SimpleGrantedAuthority(memberGeneral.getRole()));
//        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
//        return List.of();
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

    public Long getConstructionId() {
        Long constructionId = null;
        if(this.memberGeneral != null && this.memberGeneral.getConstruction() != null) {
            constructionId = this.memberGeneral.getConstruction().getId();
        }

        return constructionId;
    }
}
