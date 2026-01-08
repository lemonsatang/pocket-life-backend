package com.health.pocketlife.jwt;

import com.health.pocketlife.entity.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        // 유저 권한 등록
        collection.add(new SimpleGrantedAuthority(user.getRole().toString()));
        return collection;
    }

    // 실제 암호화된 비밀번호 반환
    @Override
    public String getPassword() {
        return user.getPasswd();
    }
    //  실제 사용자 ID 반환
    @Override
    public String getUsername() {
        return user.getUsrid();
    }

    // 아래 설정들은 일단 모두 true로 반환해두면 돼
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
