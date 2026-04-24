package com.poly.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles /oauth2/authorization/{provider} gracefully when credentials
 * are not configured, instead of returning a Whitelabel 404.
 *
 * When OAuth2 IS configured, Spring Security's OAuth2LoginAuthenticationFilter
 * intercepts this URL first (higher priority) so this controller is never reached.
 * When OAuth2 is NOT configured (clientRegistrationRepository == null or empty),
 * Spring Security doesn't set up the filter, so the request falls through to here.
 */
@Controller
public class OAuth2FallbackController {

    @Autowired(required = false)
    private ClientRegistrationRepository clientRegistrationRepository;

    @GetMapping("/oauth2/authorization/{provider}")
    public String oauth2Fallback(@PathVariable String provider, RedirectAttributes ra) {
        // Check if this provider is actually registered
        if (clientRegistrationRepository != null) {
            try {
                clientRegistrationRepository.findByRegistrationId(provider);
                // If found, Spring Security should have handled it — redirect back
                ra.addFlashAttribute("errorMessage",
                    "Đăng nhập bằng " + providerName(provider) + " chưa được cấu hình đúng. Vui lòng liên hệ quản trị viên.");
                return "redirect:/signin";
            } catch (Exception ignored) {
                // Provider not found
            }
        }
        ra.addFlashAttribute("errorMessage",
            "Tính năng đăng nhập bằng " + providerName(provider) +
            " chưa được kích hoạt. Vui lòng đăng nhập bằng Email & Mật khẩu.");
        return "redirect:/signin";
    }

    private String providerName(String provider) {
        return switch (provider.toLowerCase()) {
            case "google"   -> "Google";
            case "facebook" -> "Facebook";
            default         -> provider.substring(0, 1).toUpperCase() + provider.substring(1);
        };
    }
}
