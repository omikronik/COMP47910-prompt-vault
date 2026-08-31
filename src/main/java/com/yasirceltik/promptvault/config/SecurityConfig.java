package com.yasirceltik.promptvault.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    public static final String CONTENT_SECURITY_POLICY =
            "default-src 'self'; "
            + "script-src 'self'; "
            + "style-src 'self'; "
            + "img-src 'self' data:; "
            + "font-src 'self'; "
            + "connect-src 'self'; "
            + "object-src 'none'; "
            + "base-uri 'self'; "
            + "form-action 'self'; "
            + "frame-ancestors 'none'";

    private final LoginRateLimitFilter loginRateLimitFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
                    )
            .headers(headers -> headers
                    .frameOptions(frame -> frame.deny())
                    .contentSecurityPolicy(csp -> csp
                            .policyDirectives(CONTENT_SECURITY_POLICY)))
            .addFilterBefore(
                    loginRateLimitFilter,
                    UsernamePasswordAuthenticationFilter.class
                    );

        return http.build();
    }
}
