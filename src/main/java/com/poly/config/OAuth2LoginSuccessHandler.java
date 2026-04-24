package com.poly.config;

import com.poly.entity.Users;
import com.poly.service.JWTService;
import com.poly.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();
        String registrationId = oauthToken.getAuthorizedClientRegistrationId(); // "google" or "facebook"

        Map<String, Object> attrs = oauthUser.getAttributes();

        String email = extractEmail(attrs, registrationId);
        String name = extractName(attrs);
        String oauthId = extractOAuthId(attrs, registrationId);

        if (email == null || email.isBlank()) {
            response.sendRedirect("/signin?error=oauth_no_email");
            return;
        }

        try {
            Users user = userService.findOrCreateOAuthUser(email, name, registrationId, oauthId);

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            addCookie(response, "jwt_token", accessToken, 30 * 60);
            addCookie(response, "jwt_refresh_token", refreshToken, 7 * 24 * 60 * 60);

            if (user.isAdmin()) {
                response.sendRedirect("/admin");
            } else if (user.isStaff()) {
                response.sendRedirect("/banhangtaiquay");
            } else {
                response.sendRedirect("/");
            }
        } catch (Exception e) {
            response.sendRedirect("/signin?error=oauth_failed");
        }
    }

    private String extractEmail(Map<String, Object> attrs, String provider) {
        Object email = attrs.get("email");
        return email != null ? email.toString() : null;
    }

    private String extractName(Map<String, Object> attrs) {
        Object name = attrs.get("name");
        if (name != null) return name.toString();
        String given = attrs.containsKey("given_name") ? (String) attrs.get("given_name") : "";
        String family = attrs.containsKey("family_name") ? (String) attrs.get("family_name") : "";
        return (given + " " + family).trim();
    }

    private String extractOAuthId(Map<String, Object> attrs, String provider) {
        Object sub = attrs.get("sub");
        if (sub != null) return sub.toString();
        Object id = attrs.get("id");
        return id != null ? id.toString() : null;
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
        response.addHeader("Set-Cookie",
                String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly; SameSite=Lax", name, value, maxAge));
    }
}
