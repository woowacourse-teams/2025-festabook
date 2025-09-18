package com.daedan.festabook.global.security.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.daedan.festabook.global.security.role.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JwtProviderTest {
    
    private static final String VALID_KEY = "dGVzdC10ZXN0LXNlY3JldC1rZXktMzJieXRlcy0xMjM0NTY3OA=="; // gitleaks:allow unit-test secret
    private static final String OTHER_KEY = "b3RoZXItdGVzdC1zZWNyZXQta2V5LTMyYnl0ZXMtMTIzNDU2Nzg="; // gitleaks:allow unit-test secret
    private static final String USERNAME = "council";
    private static final Long FESTIVAL_ID = 1L;
    private static final long EXPIRY = TimeUnit.MINUTES.toMillis(10);

    private JwtProvider newProvider(String key, long validityMs) {
        return new JwtProvider(key, validityMs);
    }

    @Nested
    class createToken {

        @Test
        void 성공() {
            // given
            String username = "council";
            Long festivalId = 1L;
            Set<RoleType> roleTypes = Set.of(RoleType.ROLE_COUNCIL);
            JwtProvider jwtProvider = newProvider(VALID_KEY, EXPIRY);

            // when
            String token = jwtProvider.createToken(username, festivalId, roleTypes);

            // then
            Claims claims = jwtProvider.extractBody(token);

            assertSoftly(s -> {
                s.assertThat(token).isNotNull();
                s.assertThat(username).isEqualTo(claims.getSubject());
                s.assertThat(claims.getExpiration()).isNotNull();
                s.assertThat(claims.getExpiration().getTime())
                        .isCloseTo(System.currentTimeMillis() + EXPIRY, within(10000L));
            });
        }

        @Test
        void 성공_여러개의_ROLE_가진_경우() {
            // given
            String username = "council";
            Long festivalId = 1L;
            Set<RoleType> roleTypes = Set.of(RoleType.ROLE_COUNCIL, RoleType.ROLE_ADMIN);
            JwtProvider jwtProvider = newProvider(VALID_KEY, EXPIRY);

            // when
            String token = jwtProvider.createToken(username, festivalId, roleTypes);

            // then
            Claims claims = jwtProvider.extractBody(token);

            assertSoftly(s -> {
                s.assertThat(token).isNotNull();
                s.assertThat(username).isEqualTo(claims.getSubject());
                s.assertThat(claims.getExpiration()).isNotNull();
                s.assertThat(claims.getExpiration().getTime())
                        .isCloseTo(System.currentTimeMillis() + EXPIRY, within(10000L));
            });
        }
    }

    @Nested
    class extractBody {

        @Test
        void 성공_유효한_토큰() {
            // given
            String username = "council";
            Long festivalId = 1L;
            Set<RoleType> roleTypes = Set.of(RoleType.ROLE_COUNCIL);
            JwtProvider jwtProvider = newProvider(VALID_KEY, TimeUnit.MINUTES.toMillis(5));
            String token = jwtProvider.createToken(username, festivalId, roleTypes);

            // when
            Claims claims = jwtProvider.extractBody(token);

            // then
            assertThat(username).isEqualTo(claims.getSubject());
        }

        @Test
        void 예외_만료된_토큰() {
            // given
            JwtProvider jwtProvider = newProvider(VALID_KEY, 0);
            Set<RoleType> roleTypes = Set.of(RoleType.ROLE_COUNCIL);
            String token = jwtProvider.createToken(USERNAME, FESTIVAL_ID, roleTypes);

            // when & then
            assertThatThrownBy(() -> jwtProvider.extractBody(token))
                    .isInstanceOf(ExpiredJwtException.class);
        }
    }

    @Nested
    class isValidToken {

        @Test
        void 성공_유효한_토큰_true() {
            // given
            JwtProvider jwtProvider = newProvider(VALID_KEY, TimeUnit.MINUTES.toMillis(1L));
            Set<RoleType> roleTypes = Set.of(RoleType.ROLE_COUNCIL);
            String token = jwtProvider.createToken(USERNAME, FESTIVAL_ID, roleTypes);

            // then
            boolean result = jwtProvider.isValidToken(token);

            // when
            assertThat(result).isTrue();
        }

        @Test
        void 성공_만료된_토큰_false() {
            // given
            JwtProvider jwtProvider = newProvider(VALID_KEY, TimeUnit.MINUTES.toMillis(0));
            Set<RoleType> roleTypes = Set.of(RoleType.ROLE_COUNCIL);
            String token = jwtProvider.createToken(USERNAME, FESTIVAL_ID, roleTypes);

            // then
            boolean result = jwtProvider.isValidToken(token);

            // when
            assertThat(result).isFalse();
        }

        @Test
        void 성공_서명키_불일치_false() {
            // given
            JwtProvider jwtProvider = newProvider(VALID_KEY, TimeUnit.MINUTES.toMillis(1));
            Set<RoleType> roleTypes = Set.of(RoleType.ROLE_COUNCIL);
            String token = jwtProvider.createToken(USERNAME, FESTIVAL_ID, roleTypes);

            JwtProvider jwtProviderWithOtherKey = newProvider(OTHER_KEY, TimeUnit.MINUTES.toMillis(1));

            // then
            boolean result = jwtProviderWithOtherKey.isValidToken(token);

            // when
            assertThat(result).isFalse();
        }

        @Test
        void 성공_형식_손상된_토큰_false() {
            // given
            JwtProvider jwtProvider = newProvider(VALID_KEY, TimeUnit.MINUTES.toMillis(1));
            Set<RoleType> roleTypes = Set.of(RoleType.ROLE_COUNCIL);
            String token = jwtProvider.createToken(USERNAME, FESTIVAL_ID, roleTypes) + "corrupted";

            // then
            boolean result = jwtProvider.isValidToken(token);

            // when
            assertThat(result).isFalse();
        }
    }

    @Nested
    class extractRoles {

        @Test
        void 성공() {
            // given
            Set<RoleType> roleTypes = Set.of(RoleType.ROLE_COUNCIL);
            JwtProvider jwtProvider = newProvider(VALID_KEY, TimeUnit.MINUTES.toMillis(5));
            String token = jwtProvider.createToken("", 1L, roleTypes);
            Claims claims = jwtProvider.extractBody(token);
            System.out.println(token);

            // when
            Set<RoleType> result = jwtProvider.extractRoles(claims);

            // then
            assertThat(result).isEqualTo(roleTypes);
        }

        @Test
        void 성공_ROLE_여러개인_경우() {
            // given
            Set<RoleType> roleTypes = Set.of(RoleType.ROLE_COUNCIL, RoleType.ROLE_ADMIN);
            JwtProvider jwtProvider = newProvider(VALID_KEY, TimeUnit.MINUTES.toMillis(5));
            String token = jwtProvider.createToken("", 1L, roleTypes);
            Claims claims = jwtProvider.extractBody(token);

            // when
            Set<RoleType> result = jwtProvider.extractRoles(claims);

            // then
            assertThat(result).isEqualTo(roleTypes);
        }
    }

    @Nested
    class extractFestivalId {
        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            JwtProvider jwtProvider = newProvider(VALID_KEY, TimeUnit.MINUTES.toMillis(5));
            String token = jwtProvider.createToken("", festivalId, Set.of(RoleType.ROLE_COUNCIL));
            Claims claims = jwtProvider.extractBody(token);

            // when
            Long result = jwtProvider.extractFestivalId(claims);

            // then
            assertThat(result).isEqualTo(festivalId);
        }
    }
}
