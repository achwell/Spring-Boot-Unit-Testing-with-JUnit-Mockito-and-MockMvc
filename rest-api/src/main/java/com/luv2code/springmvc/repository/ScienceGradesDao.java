package com.luv2code.springmvc.repository;

import com.luv2code.springmvc.models.ScienceGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScienceGradesDao extends JpaRepository<ScienceGrade, Integer> {

    Iterable<ScienceGrade> findGradeByStudentId (int id);

    void deleteByStudentId(int id);
}
