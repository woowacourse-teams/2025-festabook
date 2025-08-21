package com.daedan.festabook.global.security.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JwtProviderTest {

    private static final String CLAIM_FESTIVAL_ID = "festivalId";
    private static final String VALID_KEY = "dGVzdC10ZXN0LXNlY3JldC1rZXktMzJieXRlcy0xMjM0NTY3OA=="; // gitleaks:allow unit-test secret
    private static final String OTHER_KEY = "b3RoZXItdGVzdC1zZWNyZXQta2V5LTMyYnl0ZXMtMTIzNDU2Nzg="; // gitleaks:allow unit-test secret
    private static final String USERNAME = "council";
    private static final Long FESTIVAL_ID = 1L;

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
            JwtProvider jwtProvider = newProvider(VALID_KEY, TimeUnit.MINUTES.toMillis(10));

            // when
            String token = jwtProvider.createToken(username, festivalId);

            // then
            Claims claims = jwtProvider.extractBody(token);

            assertSoftly(s -> {
                s.assertThat(token).isNotNull();
                s.assertThat(username).isEqualTo(claims.getSubject());
                s.assertThat(festivalId).isEqualTo(claims.get(CLAIM_FESTIVAL_ID, Long.class));
                s.assertThat(claims.getExpiration()).isNotNull();
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
            JwtProvider jwtProvider = newProvider(VALID_KEY, TimeUnit.MINUTES.toMillis(5));
            String token = jwtProvider.createToken(username, festivalId);

            // when
            Claims claims = jwtProvider.extractBody(token);

            // then
            assertSoftly(s -> {
                s.assertThat(username).isEqualTo(claims.getSubject());
                s.assertThat(festivalId).isEqualTo(claims.get(CLAIM_FESTIVAL_ID, Long.class));
            });
        }

        @Test
        void 예외_만료된_토큰() {
            // given
            JwtProvider jwtProvider = newProvider(VALID_KEY, 0);
            String token = jwtProvider.createToken(USERNAME, FESTIVAL_ID);

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
            String token = jwtProvider.createToken(USERNAME, FESTIVAL_ID);

            // then
            boolean result = jwtProvider.isValidToken(token);

            // when
            assertThat(result).isTrue();
        }

        @Test
        void 성공_만료된_토큰_false() {
            // given
            JwtProvider jwtProvider = newProvider(VALID_KEY, TimeUnit.MINUTES.toMillis(0));
            String token = jwtProvider.createToken(USERNAME, FESTIVAL_ID);

            // then
            boolean result = jwtProvider.isValidToken(token);

            // when
            assertThat(result).isFalse();
        }

        @Test
        void 성공_서명키_불일치_false() {
            // given
            JwtProvider jwtProvider = newProvider(VALID_KEY, TimeUnit.MINUTES.toMillis(1));
            String token = jwtProvider.createToken(USERNAME, FESTIVAL_ID);

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
            String token = jwtProvider.createToken(USERNAME, FESTIVAL_ID) + "corrupted";

            // then
            boolean result = jwtProvider.isValidToken(token);

            // when
            assertThat(result).isFalse();
        }
    }
}
