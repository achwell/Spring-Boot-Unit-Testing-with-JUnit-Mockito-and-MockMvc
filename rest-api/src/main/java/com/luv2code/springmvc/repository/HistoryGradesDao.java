package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.HistoryGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryGradesDao extends JpaRepository<HistoryGrade, Integer> {

    Iterable<HistoryGrade> findGradeByStudentId (int id);

    void deleteByStudentId(int id);
}
