package com.team.docrate.global.security;

import com.team.docrate.domain.user.repository.UserRepository;
import com.team.docrate.global.security.jwt.JwtAuthenticationFilter;
import com.team.docrate.global.security.jwt.JwtTokenProvider;
import com.team.docrate.infra.redis.AccessTokenBlacklistService;
import com.team.docrate.infra.redis.RefreshTokenRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisService refreshTokenRedisService;
    private final AccessTokenBlacklistService accessTokenBlacklistService;
    private final UserRepository userRepository;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(
                jwtTokenProvider,
                refreshTokenRedisService,
                accessTokenBlacklistService,
                userRepository
        );
    }

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                		.requestMatchers(
                		        "/", "/signup", "/login", "/logout",
                		        "/search",
                		        "/css/**", "/js/**", "/images/**",
                		        "/favicon.ico"
                		).permitAll()
                		.requestMatchers(
                                "/doctors",
                                "/doctors/*",
                                "/doctors/*/reviews",
                                "/hospitals",
                                "/hospitals/*"
                            ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/mypage/**", "/doctor-requests/new", "/doctor-requests").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler())
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}