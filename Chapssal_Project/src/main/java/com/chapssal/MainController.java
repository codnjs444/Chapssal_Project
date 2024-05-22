package com.chapssal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String showMainPage() {
        return "home"; // home.html 파일을 렌더링
    }
}
