package com.daedan.festabook.global.security.council;

import com.daedan.festabook.council.domain.Council;
import java.util.Collection;
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

    private final Council council;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> roles = council.getRoles();
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return council.getPassword();
    }

    @Override
    public String getUsername() {
        return council.getUsername();
    }

    public Long getFestivalId() {
        return council.getFestival().getId();
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
