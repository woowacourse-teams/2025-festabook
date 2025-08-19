package com.daedan.festabook.global.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private static final String CLAIM_FESTIVAL_ID = "festivalId";

    private final Key key;
    private final long validityInMilliseconds;
    private final JwtParser jwtParser;

    public JwtProvider(
            @Value("${secret.jwt-key}") final String secretKey,
            @Value("${secret.jwt-expiration}") final long validityInMilliseconds
    ) {
        this.validityInMilliseconds = validityInMilliseconds;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    public String createToken(String username, Long festivalId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .claim(CLAIM_FESTIVAL_ID, festivalId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractBody(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public boolean isValidToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
