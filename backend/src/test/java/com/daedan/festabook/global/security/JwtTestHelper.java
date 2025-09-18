package com.daedan.festabook.global.security;

import com.daedan.festabook.council.domain.Council;
import com.daedan.festabook.council.infrastructure.CouncilJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.role.RoleType;
import com.daedan.festabook.global.security.util.JwtProvider;
import com.google.common.net.HttpHeaders;
import io.restassured.http.Header;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTestHelper {

    private static final String BEARER_PREFIX = "Bearer ";

    private final CouncilJpaRepository councilRepository;
    private final FestivalJpaRepository festivalRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder encoder;

    @Transactional
    public Header createAuthorizationHeader(Festival festival) {
        UUID uuid = UUID.randomUUID();
        String randomUsername = "test_" + uuid;
        String randomPassword = "password_" + uuid;

        festivalRepository.save(festival);
        Council council = new Council(festival, randomUsername, encoder.encode(randomPassword));
        council.updateRole(Set.of(RoleType.ROLE_COUNCIL));
        councilRepository.save(council);

        String token = jwtProvider.createToken(randomUsername, festival.getId());
        return new Header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);
    }

    @Transactional
    public Header createAdminAuthorizationHeader(Festival festival) {
        UUID uuid = UUID.randomUUID();
        String randomUsername = "test_" + uuid;
        String randomPassword = "password_" + uuid;

        festivalRepository.save(festival);
        Council council = new Council(festival, randomUsername, encoder.encode(randomPassword));
        council.updateRole(Set.of(RoleType.ROLE_ADMIN));
        councilRepository.save(council);

        String token = jwtProvider.createToken(randomUsername, festival.getId());
        return new Header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);
    }

    @Transactional
    public Header createAuthorizationHeaderWithRole(Festival festival, RoleType roleType) {
        UUID uuid = UUID.randomUUID();
        String randomUsername = "test_" + uuid;
        String randomPassword = "password_" + uuid;

        festivalRepository.save(festival);
        Council council = new Council(festival, randomUsername, encoder.encode(randomPassword));
        council.updateRole(Set.of(roleType));
        councilRepository.save(council);

        String token = jwtProvider.createToken(randomUsername, festival.getId());
        return new Header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);
    }

    @Transactional
    public Header createAuthorizationHeaderWithRoleAndPassword(Festival festival, RoleType roleType, String password) {
        UUID uuid = UUID.randomUUID();
        String randomUsername = "test_" + uuid;

        festivalRepository.save(festival);
        Council council = new Council(festival, randomUsername, encoder.encode(password));
        council.updateRole(Set.of(roleType));
        councilRepository.save(council);

        String token = jwtProvider.createToken(randomUsername, festival.getId());
        return new Header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);
    }
}
