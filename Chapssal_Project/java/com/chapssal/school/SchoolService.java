package com.chapssal.school;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    public List<School> findTop3Schools() {
        return schoolRepository.findTop3ByOrderBySchoolNameAsc();
    }

    public List<School> findTop3SchoolsByName(String name) {
        return schoolRepository.findTop3BySchoolNameStartingWithOrderBySchoolNameAsc(name);
    }
}