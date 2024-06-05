package com.chapssal.school;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Integer> {
    Optional<School> findBySchoolCode(String schoolCode);
    List<School> findTop3ByOrderBySchoolNameAsc();
    List<School> findTop3BySchoolNameStartingWithOrderBySchoolNameAsc(String name);
    School findBySchoolName(String schoolName);
}
