package com.daedan.festabook.global.security.filter;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.global.security.council.CouncilDetails;
import com.daedan.festabook.global.security.council.CouncilDetailsService;
import com.daedan.festabook.global.security.util.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHENTICATION_HEADER = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Bearer ";

    private final JwtProvider jwtProvider;
    private final CouncilDetailsService councilDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String accessToken = extractTokenFromHeader(request);

            if (accessToken != null && jwtProvider.isValidToken(accessToken)) {
                String username = jwtProvider.extractBody(accessToken).getSubject();

                CouncilDetails councilDetails = (CouncilDetails) councilDetailsService.loadUserByUsername(username);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        councilDetails, null, councilDetails.getAuthorities());

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
