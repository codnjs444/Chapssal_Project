package com.chapssal.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User;
        try {
            oAuth2User = super.loadUser(userRequest);
        } catch (OAuth2AuthenticationException e) {
            log.error("Failed to load user from OAuth2 provider", e);
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token", "Failed to load user from OAuth2 provider", null), e);
        }

        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.debug("OAuth2 User Attributes: {}", attributes);

        try {
            String providerUserId = attributes.get("id").toString();
            String username = ((Map<String, Object>) attributes.get("properties")).get("nickname").toString();

            User user = userRepository.findByUserId(providerUserId).orElseGet(() -> {
                User newUser = new User();
                newUser.setUserId(providerUserId);
                newUser.setUserName(username);
                newUser.setCreateDate(LocalDateTime.now());
                log.debug("New User: {}", newUser);
                return userRepository.save(newUser);
            });

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "id");
        } catch (Exception e) {
            log.error("Error loading user information", e);
            OAuth2Error error = new OAuth2Error("invalid_token", "Failed to obtain user details from provider", null);
            throw new OAuth2AuthenticationException(error, e);
        }
    }
}
