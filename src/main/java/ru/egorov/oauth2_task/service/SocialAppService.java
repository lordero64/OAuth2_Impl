package ru.egorov.oauth2_task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocialAppService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Set<String> ADMIN_LOGINS = Set.of("lordero64");

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        log.info("Аутентификация по средствам GitHub");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegatedService = new DefaultOAuth2UserService();
        OAuth2User OA2user = delegatedService.loadUser(userRequest);

        String login = OA2user.getAttribute("login");

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (ADMIN_LOGINS.contains(login)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            log.info("Пользователю {} назначена роль Админ", login);
        }

        log.info("АУТЕНТИФИКАЦИЯ УСПЕШНА: {} (login: {})", OA2user.getAttribute("name"), login);

        Map<String, Object> attributes = OA2user.getAttributes();

        return new DefaultOAuth2User(authorities, attributes, "login");
    }
}
