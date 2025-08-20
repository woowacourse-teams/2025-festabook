package com.daedan.festabook.council.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.global.security.role.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Council {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<RoleType> roles = new HashSet<>();

    protected Council(
            Long id,
            Festival festival,
            String username,
            String password
    ) {
        validateFestival(festival);
        validateUsername(username);
        validatePassword(password);

        this.id = id;
        this.festival = festival;
        this.username = username;
        this.password = password;
    }

    public Council(
            Festival festival,
            String username,
            String password
    ) {
        this(
                null,
                festival,
                username,
                password
        );
    }

    public void updateRole(Set<RoleType> roles) {
        if (roles == null || roles.isEmpty()) {
            return;
        }

        roles.stream()
                .filter(Objects::nonNull)
                .forEach(this.roles::add);
    }

    private void validateFestival(Festival festival) {
        if (festival == null) {
            throw new BusinessException("축제는 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("아이디는 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException("비밀번호는 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
