package com.daedan.festabook.global.security.config;

import com.daedan.festabook.global.security.filter.JwtAuthenticationFilter;
import com.daedan.festabook.global.security.handler.CustomAccessDeniedHandler;
import com.daedan.festabook.global.security.handler.CustomAuthenticationEntryPoint;
import com.daedan.festabook.global.security.role.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
            "/api-docs/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
    };

    private static final String[] GET_WHITELIST = {
            "/event-dates",
            "/event-dates/*/events",
            "/announcements",
            "/places",
            "/places/*",
            "/places/*/announcements",
            "/places/previews",
            "/places/geographies",
            "/festivals",
            "/festivals/universities",
            "/festivals/geography",
            "/festivals/notifications/*",
            "/festivals/lost-item-guide",
            "/lost-items",
            "/questions",
            "/lineups",
            "/time-tags",
            "/actuator/health"
    };

    private static final String[] POST_WHITELIST = {
            "/devices",
            "/festivals/*/notifications",
            "/festivals/*/notifications/*",
            "/places/*/favorites",
            "/councils/login"
    };

    private static final String[] PATCH_WHITELIST = {
            "/devices/*"
    };

    private static final String[] DELETE_WHITELIST = {
            "/festivals/notifications/*",
            "/places/favorites/*"
    };

    private final CorsFilter corsFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        defaultSecuritySetting(http);

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.GET, GET_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.POST, POST_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.PATCH, PATCH_WHITELIST).permitAll()
                        .requestMatchers(HttpMethod.DELETE, DELETE_WHITELIST).permitAll()
                        .anyRequest().hasAnyAuthority(
                                RoleType.ROLE_COUNCIL.name(),
                                RoleType.ROLE_ADMIN.name()
                        )
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    private void defaultSecuritySetting(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
    }
}
