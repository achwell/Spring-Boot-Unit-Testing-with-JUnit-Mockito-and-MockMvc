package com.luv2code.springmvc.service;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
public class StudentAndGradeService {

    private final StudentDao studentDao;
    private final MathGradeDao mathGradeDao;

    private final ScienceGradeDao scienceGradeDao;

    private final HistoryGradeDao historyGradeDao;

    public StudentAndGradeService(StudentDao studentDao, MathGradeDao mathGradeDao, ScienceGradeDao scienceGradeDao, HistoryGradeDao historyGradeDao) {
        this.studentDao = studentDao;
        this.mathGradeDao = mathGradeDao;
        this.scienceGradeDao = scienceGradeDao;
        this.historyGradeDao = historyGradeDao;
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
                    return updateGrade(new MathGrade(), grade, studentId, mathGradeDao);
                }
                case "history" -> {
                    return updateGrade(new HistoryGrade(), grade, studentId, historyGradeDao);
                }
                case "science" -> {
                    return updateGrade(new ScienceGrade(), grade, studentId, scienceGradeDao);
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
        StudentGrades studentGrades = new StudentGrades();
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

    public void configureStudentInformationModel(int studentId, Model m) {
        GradebookCollegeStudent studentEntity = studentInformation(studentId);
        StudentGrades studentGrades = studentEntity.getStudentGrades();
        m.addAttribute("student", studentEntity);
        addAverageValues(m, studentGrades.getMathGradeResults(), "mathAverage", studentGrades::findGradePointAverage);
        addAverageValues(m, studentGrades.getScienceGradeResults(), "scienceAverage", studentGrades::findGradePointAverage);
        addAverageValues(m, studentGrades.getHistoryGradeResults(), "historyAverage", studentGrades::findGradePointAverage);
    }


    private static void addAverageValues(Model m, List<Grade> gradeResults, String attributeName, Function<List<Grade>, Double> fn) {
        if(gradeResults.size() > 0) {
            m.addAttribute(attributeName, fn.apply(gradeResults));
        } else {
            m.addAttribute(attributeName, "N/A");
        }
    }


    private static <T extends Grade> List<Grade> getGrades(Iterable<T> grades) {
        List<Grade> gradesList = new ArrayList<>();
        grades.forEach(gradesList::add);
        return gradesList;
    }

    private static <T extends Grade> boolean updateGrade(T grade, double gradeValue, int studentId, JpaRepository<T, Integer> repository) {
        grade.setId(0);
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
