package com.daedan.festabook.global.security.council;

import com.daedan.festabook.global.security.role.RoleType;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class CouncilDetails implements UserDetails {

    private final String username;
    private final Long festivalId;
    private final Set<RoleType> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .filter(Objects::nonNull)
                .map(RoleType::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        throw new UnsupportedOperationException("잘못된 사용입니다.");
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Long getFestivalId() {
        return festivalId;
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
}
