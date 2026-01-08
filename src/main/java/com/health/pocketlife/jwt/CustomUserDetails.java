package com.health.pocketlife.jwt;

import com.health.pocketlife.entity.User;
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

        String role = user.getRole().toString(); // 예: "USER"

        // [수정 포인트] 시큐리티의 hasRole("USER")은 "ROLE_USER"를 검사합니다.
        // 접두사가 없다면 강제로 붙여서 권한을 등록합니다.
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        collection.add(new SimpleGrantedAuthority(role));
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPasswd();
    }

    @Override
    public String getUsername() {
        return user.getUsrid();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
