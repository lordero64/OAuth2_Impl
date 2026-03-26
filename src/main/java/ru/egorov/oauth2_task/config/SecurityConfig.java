package ru.egorov.oauth2_task.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import ru.egorov.oauth2_task.handler.CustomAccessDeniedHandler;
import ru.egorov.oauth2_task.handler.OAuth2LogoutSuccessHandler;
import ru.egorov.oauth2_task.service.SocialAppService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final SocialAppService socialAppService;
    private final OAuth2LogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/index.html", "/error").permitAll()
                        .requestMatchers("/admin.html", "/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())

                .exceptionHandling(ex -> ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler(accessDeniedHandler))

                .oauth2Login(oauth2 -> oauth2.loginPage("/index.html")
                        .userInfoEndpoint(userInfo -> userInfo.userService(socialAppService))
                        .defaultSuccessUrl("/user.html", true))

                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler).invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}
