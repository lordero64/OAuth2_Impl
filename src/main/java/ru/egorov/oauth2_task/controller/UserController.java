package ru.egorov.oauth2_task.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    @GetMapping("/api/user")
    public ResponseEntity<Map<String, Object>> user(@AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) {
            log.warn("Попытка доступа к /api/user без аутентификации");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated"));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // ✅ БЕЗОПАСНОЕ получение атрибутов
        String login = principal.getAttribute("login");
        String name = principal.getAttribute("name");
        String avatarUrl = principal.getAttribute("avatar_url");

        Map<String, Object> profile = new HashMap<>();
        profile.put("login", login != null ? login : "unknown");
        profile.put("name", name != null ? name : "unknown");
        profile.put("avatarUrl", avatarUrl != null ? avatarUrl : "");
        profile.put("isAdmin", isAdmin);

        return ResponseEntity.ok()
                .header("X-User-Role", isAdmin ? "ADMIN" : "USER")
                .header("X-User-Login", login != null ? login : "")
                .body(profile);
    }

    @GetMapping("/api/admin/status")
    public ResponseEntity<Map<String, String>> adminStatus() {
        return ResponseEntity.ok()
                .header("X-Admin-Access", "granted")
                .body(Map.of(
                        "status", "OK",
                        "message", "Доступ к админ-панели разрешен",
                        "timestamp", String.valueOf(System.currentTimeMillis())
                ));
    }
}
