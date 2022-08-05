package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    private final StudentDao studentDao;
    private final MathGradeDao mathGradeDao;

    private final ScienceGradeDao scienceGradeDao;

    private final HistoryGradeDao historyGradeDao;

    private final MathGrade mathGrade;

    private final ScienceGrade scienceGrade;

    private final HistoryGrade historyGrade;

    private final StudentGrades studentGrades;

    public StudentAndGradeService(StudentDao studentDao, MathGradeDao mathGradeDao, ScienceGradeDao scienceGradeDao, HistoryGradeDao historyGradeDao, MathGrade mathGrade, ScienceGrade scienceGrade, HistoryGrade historyGrade, StudentGrades studentGrades) {
        this.studentDao = studentDao;
        this.mathGradeDao = mathGradeDao;
        this.scienceGradeDao = scienceGradeDao;
        this.historyGradeDao = historyGradeDao;
        this.mathGrade = mathGrade;
        this.scienceGrade = scienceGrade;
        this.historyGrade = historyGrade;
        this.studentGrades = studentGrades;
    }

    public void createStudent(String firstname, String lastname, String emailAddress) {
        CollegeStudent student = new CollegeStudent(firstname, lastname, emailAddress);
        student.setId(0);
        studentDao.save(student);
    }

    public boolean checkIfStudentIsNull(int id) {
        return studentDao.findById(id).isEmpty();
    }

    public void delete(int id) {
        if (!checkIfStudentIsNull(id)) {
            studentDao.deleteById(id);
            mathGradeDao.deleteByStudentId(id);
            scienceGradeDao.deleteByStudentId(id);
            historyGradeDao.deleteByStudentId(id);
        }
    }

    public Iterable<CollegeStudent> getGradebook() {
        return studentDao.findAll();
    }

    public boolean createGrade(double grade, int studentId, String gradeType) {
        if (checkIfStudentIsNull(studentId)) {
            return false;
        }
        if (grade >= 0 && grade <= 100) {
            switch (gradeType) {
                case "math" -> {
                    return updateGrade(mathGrade, grade, studentId, mathGradeDao);
                }
                case "history" -> {
                    return updateGrade(historyGrade, grade, studentId, historyGradeDao);
                }
                case "science" -> {
                    return updateGrade(scienceGrade, grade, studentId, scienceGradeDao);
                }
                default -> {
                    return false;
                }
            }
        }
        return false;
    }

    public int deleteGrade(int gradeId, String gradeType) {
        int studentId = 0;
        switch (gradeType) {
            case "math" -> {
                studentId = deleteGrade(gradeId, mathGradeDao);
            }
            case "history" -> {
                studentId = deleteGrade(gradeId, historyGradeDao);
            }
            case "science" -> {
                studentId = deleteGrade(gradeId, scienceGradeDao);
            }
        }
        return studentId;
    }

    public GradebookCollegeStudent studentInformation(int studentId) {
        Optional<CollegeStudent> student = studentDao.findById(studentId);
        if (student.isEmpty()) {
            return null;
        }
        List<Grade> mathGradesList = getGrades(mathGradeDao.findGradeByStudentId(studentId));
        List<Grade> scienceGradesList = getGrades(scienceGradeDao.findGradeByStudentId(studentId));
        List<Grade> historyGradesList = getGrades(historyGradeDao.findGradeByStudentId(studentId));
        studentGrades.setMathGradeResults(mathGradesList);
        studentGrades.setScienceGradeResults(scienceGradesList);
        studentGrades.setHistoryGradeResults(historyGradesList);
        CollegeStudent collegeStudent = student.get();
        return new GradebookCollegeStudent(
                collegeStudent.getId(),
                collegeStudent.getFirstname(),
                collegeStudent.getLastname(),
                collegeStudent.getEmailAddress(),
                studentGrades
        );
    }

    private static <T extends Grade> List<Grade> getGrades(Iterable<T> grades) {
        List<Grade> gradesList = new ArrayList<>();
        grades.forEach(gradesList::add);
        return gradesList;
    }

    private static <T extends Grade> boolean updateGrade(T grade, double gradeValue, int studentId, JpaRepository<T, Integer> repository) {
        grade.setGrade(gradeValue);
        grade.setStudentId(studentId);
        repository.save(grade);
        return true;
    }

    private static <T extends Grade> int deleteGrade(Integer gradeId, JpaRepository<T, Integer> repository) {
        int studentId = 0;
        try {
            T grade = repository.getReferenceById(gradeId);
            studentId = grade.getStudentId();
            repository.delete(grade);
        } catch (EntityNotFoundException e) {
            //OK
        }
        return studentId;
    }
}
