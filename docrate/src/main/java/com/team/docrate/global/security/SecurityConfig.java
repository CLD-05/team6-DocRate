package com.team.docrate.global.security;

import com.team.docrate.global.security.jwt.JwtAuthenticationFilter;
import com.team.docrate.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
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
		        .csrf(csrf -> csrf
		                .ignoringRequestMatchers("/token/reissue")
		        )
                .sessionManagement(session -> session
                		// 세션 없이 JWT만 사용
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                		// 누구나 접근 가능한 페이지
                        .requestMatchers("/", "/signup", "/login", "/hospitals/**", "/doctors/**","/token/reissue","/css/**", "/js/**", "/images/**").permitAll()
                        // 관리자 권한 필요
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 나머지 로그인 필요
                        .anyRequest().authenticated()
                )
                // 스프링 시큐리티 기본 로그인 기능 비활
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                // 인증/인가 예외 처리 연결
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler())
                )
                // JWT 필터 등록
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
