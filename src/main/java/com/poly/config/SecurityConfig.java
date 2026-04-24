package com.poly.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.poly.filter.JwtAuthenticationFilter;
import com.poly.security.AccessDeniedHandlerImpl;
import com.poly.filter.SecurityDiagnosticsFilter;
import com.poly.security.JwtAuthEntryPoint;
import com.poly.filter.SessionUserPromotionFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;
    private final SecurityDiagnosticsFilter securityDiagnosticsFilter;
    private final SessionUserPromotionFilter sessionUserPromotionFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Autowired(required = false)
    private ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(
            JwtAuthEntryPoint jwtAuthEntryPoint,
            AccessDeniedHandlerImpl accessDeniedHandler,
            SecurityDiagnosticsFilter securityDiagnosticsFilter,
            SessionUserPromotionFilter sessionUserPromotionFilter,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.securityDiagnosticsFilter = securityDiagnosticsFilter;
        this.sessionUserPromotionFilter = sessionUserPromotionFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    public CookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new CookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/signup", "/signin", "/active-account", "/forgot-password",
                                "/reset-password", "/access-denied", "/error", "/api/auth/refresh",
                                "/api/chatbot/**", "/api/support/**", "/api/payments/**", "/api/search/**", "/api/recommend/**",
                                "/guest/**", "/cart", "/quick-buy", "/san-pham-moi", "/khuyen-mai", "/tin-tuc", "/tin-tuc/**",
                                "/lien-he", "/loai-all", "/loai", "/sanpham", "/search",
                                "/css/**", "/js/**", "/img/**", "/image/**", "/fragment/**",
                                "/oauth2/**", "/login/oauth2/**", "/api/news/**", "/api/diem/**")
                        .permitAll()
                        .requestMatchers("/banhangtaiquay/**", "/admin/hoadon/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated());

        if (clientRegistrationRepository != null) {
            http.oauth2Login(oauth2 -> oauth2
                    .authorizationEndpoint(auth -> auth
                            .authorizationRequestRepository(cookieAuthorizationRequestRepository()))
                    .successHandler(oAuth2LoginSuccessHandler));
        }

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(sessionUserPromotionFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(securityDiagnosticsFilter, SessionUserPromotionFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
