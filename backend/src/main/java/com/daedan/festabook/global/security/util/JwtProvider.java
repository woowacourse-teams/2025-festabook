package com.daedan.festabook.global.security.util;

import com.daedan.festabook.global.security.role.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private static final String CLAIM_FESTIVAL_ID = "festivalId";
    private static final String CLAIM_ROLES_TYPE = "roles";

    private final SecretKey key;
    private final long validityInMilliseconds;
    private final JwtParser jwtParser;

    public JwtProvider(
            @Value("${secret.jwt-key}") final String secretKey,
            @Value("${secret.jwt-expiration}") final long validityInMilliseconds
    ) {
        this.validityInMilliseconds = validityInMilliseconds;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }

    public String createToken(String username, Long festivalId, Set<RoleType> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_FESTIVAL_ID, festivalId)
                .claim(CLAIM_ROLES_TYPE, roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public Claims extractBody(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    public Set<RoleType> extractRoles(Claims claims) {
        return (Set<RoleType>) claims.get(CLAIM_ROLES_TYPE, List.class).stream()
                .map(this::parseRoleType)
                .collect(Collectors.toSet());
    }

    private RoleType parseRoleType(Object role) {
        String roleTypeName = String.valueOf(role);
        return RoleType.valueOf(roleTypeName);
    }

    public Long extractFestivalId(Claims claims) {
        return claims.get(CLAIM_FESTIVAL_ID, Long.class);
    }

    public boolean isValidToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
