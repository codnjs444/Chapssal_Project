package com.chapssal.user;

import com.chapssal.school.SchoolRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private SchoolRepository schoolRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userId = oAuth2User.getAttribute("id").toString();
        
        // Get or create user from the database
        User user = userService.findByUserId(userId);

        if (user.getSchool() == null) {
            // 학교 코드가 없을 경우 학교 코드 입력 페이지로 리디렉션
            response.sendRedirect("/user/enterSchoolCode");
        } else {
            // 학교 코드가 있을 경우 기본 페이지로 리디렉션
            response.sendRedirect("/");
        }
    }
}
