package ru.egorov.oauth2_task.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {

        String username = authentication != null ? authentication.getName() : "anonymous";
        log.info("ВЫХОД ИЗ СИСТЕМЫ: {}", username);

        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            String login = oauth2User.getAttribute("login");
            log.info("ОТЗЫВ ДОСТУПА: пользователь {} выходит", login);

            response.sendRedirect("https://github.com/logout");
            return;
        }

        response.sendRedirect("/index.html");
    }
}
