package com.chapssal.school;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @GetMapping("/school/inputsuggestions")
    @ResponseBody
    public List<School> getSuggestions(@RequestParam(required = false) String query) {
        if (query == null || query.isEmpty()) {
            return schoolService.findTop3Schools();
        } else {
            return schoolService.findTop3SchoolsByName(query);
        }
    }
}