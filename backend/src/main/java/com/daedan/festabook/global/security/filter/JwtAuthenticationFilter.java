package com.daedan.festabook.global.security.filter;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.global.security.council.CouncilDetails;
import com.daedan.festabook.global.security.role.RoleType;
import com.daedan.festabook.global.security.util.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHENTICATION_HEADER = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String accessToken = extractTokenFromHeader(request);

            if (accessToken != null && jwtProvider.isValidToken(accessToken)) {
                Claims claims = jwtProvider.extractBody(accessToken);
                String username = claims.getSubject();
                Set<RoleType> roleTypes = jwtProvider.extractRoles(claims);
                Long festivalId = jwtProvider.extractFestivalId(claims);

                UserDetails userDetails = new CouncilDetails(username, festivalId, roleTypes);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (AuthenticationException | BusinessException e) {
            SecurityContextHolder.clearContext();
            throw e;
        }
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader(AUTHENTICATION_HEADER);
        if (StringUtils.hasText(token) && token.startsWith(AUTHENTICATION_SCHEME)) {
            return token.substring(AUTHENTICATION_SCHEME.length());
        }
        return null;
    }
}
