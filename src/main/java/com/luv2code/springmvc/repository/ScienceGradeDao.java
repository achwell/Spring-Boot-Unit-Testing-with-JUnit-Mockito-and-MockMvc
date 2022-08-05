package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.ScienceGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScienceGradeDao extends JpaRepository<ScienceGrade, Integer> {
    Iterable<ScienceGrade> findGradeByStudentId(int studentId);

    void deleteByStudentId(int studentId);
}
