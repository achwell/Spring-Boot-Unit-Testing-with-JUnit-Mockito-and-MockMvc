package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.StreamSupport.stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@TestPropertySource("/application-test.properties")
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @BeforeEach
    public void setupDatabase() {
        studentDao.save(new CollegeStudent("Eric", "Roby", "eric_roby@luv2code_school.com"));
        mathGradeDao.save(new MathGrade(1, 1, 100.00));
        scienceGradeDao.save(new ScienceGrade(1, 1, 100.00));
        historyGradeDao.save(new HistoryGrade(1, 1, 100.00));
    }

    @Test
    public void createStudentService() {
        studentService.createStudent("Axel", "Wulff", "axelwulff@mac.com");
        CollegeStudent collegeStudent = studentDao.findByEmailAddress("axelwulff@mac.com");
        assertEquals("axelwulff@mac.com", collegeStudent.getEmailAddress());
    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(1);
        Optional<MathGrade> deletedMathGrade = mathGradeDao.findById(1);
        Optional<HistoryGrade> deletedHistoryGrade = historyGradeDao.findById(1);
        Optional<ScienceGrade> deletedScienceGrade = scienceGradeDao.findById(1);
        assertTrue(deletedCollegeStudent.isPresent(), "Return True");
        assertTrue(deletedMathGrade.isPresent(), "Return True");
        assertTrue(deletedHistoryGrade.isPresent(), "Return True");
        assertTrue(deletedScienceGrade.isPresent(), "Return True");

        studentService.delete(1);

        deletedCollegeStudent = studentDao.findById(1);
        deletedMathGrade = mathGradeDao.findById(1);
        deletedHistoryGrade = historyGradeDao.findById(1);
        deletedScienceGrade = scienceGradeDao.findById(1);
        assertFalse(deletedCollegeStudent.isPresent(), "Return False");
        assertFalse(deletedMathGrade.isPresent(), "Return False");
        assertFalse(deletedHistoryGrade.isPresent(), "Return False");
        assertFalse(deletedScienceGrade.isPresent(), "Return False");
    }

    @Test
    @Sql("/insertData.sql")
    public void getGradebookService() {
        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradebook();
        List<CollegeStudent> collegeStudents = new ArrayList<>();
        for (CollegeStudent collegeStudent : iterableCollegeStudents) {
            collegeStudents.add(collegeStudent);
        }
        assertEquals(5, collegeStudents.size());
    }

    @Test
    public void isStudentNullCheck() {
        assertTrue(studentService.checkIfStudentIsNull(0));
        assertFalse(studentService.checkIfStudentIsNull(1));
    }

    @Test
    public void createGradeService() {
        assertTrue(studentService.createGrade(80.50, 1, "math"));
        assertTrue(studentService.createGrade(80.50, 1, "science"));
        assertTrue(studentService.createGrade(80.50, 1, "history"));
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);
        assertEquals(2, stream(mathGrades.spliterator(), false).toList().size(), "Student has 2 math grades");
        assertEquals(2, stream(scienceGrades.spliterator(), false).toList().size(), "Student has 2 science grades");
        assertEquals(2, stream(historyGrades.spliterator(), false).toList().size(), "Student has 2 history grades");
    }

    @Test
    public void createGradeServiceReturnFalse() {
        assertFalse(studentService.createGrade(-5, 1, "math"));
        assertFalse(studentService.createGrade(105, 1, "science"));
        assertFalse(studentService.createGrade(80.50, 2, "science"));
        assertFalse(studentService.createGrade(80.50, 1, "literature"));
    }

    @Test
    public void deleteGradeService() {
        assertEquals(1, studentService.deleteGrade(1, "math"), "Returns student id after delete");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdZero() {
        assertEquals(0, studentService.deleteGrade(Integer.MAX_VALUE, "science"), "No grade found");
        assertEquals(0, studentService.deleteGrade(0, "science"), "No student should have 0 id");
        assertEquals(0, studentService.deleteGrade(1, "literature"), "No student should have a literature class");
    }

    @Test
    public void studentInformation() {
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(1);
        assertNotNull(gradebookCollegeStudent);
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Eric", gradebookCollegeStudent.getFirstname());
        assertEquals("Roby", gradebookCollegeStudent.getLastname());
        assertEquals("eric_roby@luv2code_school.com", gradebookCollegeStudent.getEmailAddress());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size());
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size());
    }

    @Test
    public void studentInformationReturnNull() {
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(0);
        assertNull(gradebookCollegeStudent);
    }

}
