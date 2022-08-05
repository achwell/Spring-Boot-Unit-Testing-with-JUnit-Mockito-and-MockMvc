package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.HistoryGrade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryGradeDao extends JpaRepository<HistoryGrade, Integer> {
    Iterable<HistoryGrade> findGradeByStudentId(int studentId);

    void deleteByStudentId(int studentId);
}
