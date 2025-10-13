package com.daedan.festabook.council.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.global.domain.BaseEntity;
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
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Entity
@Getter
@SQLRestriction("deleted = false")
@SQLDelete(sql = "UPDATE council SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Council extends BaseEntity {

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

    public Council(
            Festival festival,
            String username,
            String password
    ) {
        validateFestival(festival);
        validateUsername(username);
        validatePassword(password);

        this.festival = festival;
        this.username = username;
        this.password = password;
    }

    public void updatePassword(String password) {
        validatePassword(password);

        this.password = password;
    }

    public void updateRole(Set<RoleType> roles) {
        if (!StringUtils.hasText(username)) {
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
        if (!StringUtils.hasText(username)) {
            throw new BusinessException("아이디는 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validatePassword(String password) {
        if (!StringUtils.hasText(password)) {
            throw new BusinessException("비밀번호는 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public Set<RoleType> getRoles() {
        return roles.stream()
                .collect(Collectors.toUnmodifiableSet());
    }
}
