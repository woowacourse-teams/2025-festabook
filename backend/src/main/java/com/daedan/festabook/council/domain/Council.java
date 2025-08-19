package com.daedan.festabook.council.domain;

import com.daedan.festabook.global.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.HashSet;
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

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();

    protected Council(
            Long id,
            String username,
            String password
    ) {
        validateUsername(username);
        validatePassword(password);

        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Council(
            String username,
            String password
    ) {
        this(
                null,
                username,
                password
        );
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
