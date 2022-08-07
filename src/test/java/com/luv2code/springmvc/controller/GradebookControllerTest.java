package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@TestPropertySource("/application-test.properties")
class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentAndGradeService studentService;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @BeforeAll
    public static void setup() {
        request = new MockHttpServletRequest();
        request.setParameter("firstName", "Chad");
        request.setParameter("lastName", "Darby");
        request.setParameter("emailAddress", "chad_darby@luv2code_school.com");
    }

    @BeforeEach
    public void setupDatabase() {
        studentDao.save(new CollegeStudent("Eric", "Roby", "eric_roby@luv2code_school.com"));
        mathGradeDao.save(new MathGrade(1, 1, 100.00));
        scienceGradeDao.save(new ScienceGrade(1, 1, 100.00));
        historyGradeDao.save(new HistoryGrade(1, 1, 100.00));
    }

    @Test
    void getStudentsHttpRequest() throws Exception {
        CollegeStudent studentOne = new GradebookCollegeStudent("Eric", "Roby", "eric_roby@luv2code_school.com");
        CollegeStudent studentTwo = new GradebookCollegeStudent("Chad", "Darby", "chad_darby@luv2code_school.com");
        List<CollegeStudent> collegeStudentList = of(studentOne, studentTwo);
        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);
        assertEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = mockMvc.perform(get("/")).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");
    }

    @Test
    public void createStudentHttpRequest() throws Exception {
        List<CollegeStudent> collegeStudentList = of(new CollegeStudent("Eric", "Roby", "eric_roby@luv2code_school.com"));
        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);
        assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = mockMvc
                .perform(post("/")
                        .contentType(APPLICATION_JSON)
                        .param("firstName", request.getParameterValues("firstName"))
                        .param("lastName", request.getParameterValues("lastName"))
                        .param("emailAddress", request.getParameterValues("emailAddress"))
                ).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");
        CollegeStudent verifyStudent = studentDao.findByEmailAddress("chad_darby@luv2code_school.com");
        assertNotNull(verifyStudent);
    }

    @Test
    public void deleteStudentHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());
        MvcResult mvcResult = mockMvc.perform(delete("/delete/student/1")).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");
        assertFalse(studentDao.findById(1).isPresent());
    }

    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception {
        assertFalse(studentDao.findById(0).isPresent());
        MvcResult mvcResult = mockMvc.perform(delete("/delete/student/0")).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void studentInformationHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());
        MvcResult mvcResult = mockMvc.perform(get("/studentInformation/{id}", 1)).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");
    }

    @Test
    public void studentInformationStudentDoesNotExistHttpRequest() throws Exception {
        assertFalse(studentDao.findById(0).isPresent());
        MvcResult mvcResult = mockMvc.perform(get("/studentInformation/{id}", 0)).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void createValidGradeHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());
        GradebookCollegeStudent student = studentService.studentInformation(1);
        assertEquals(1, student.getStudentGrades().getMathGradeResults().size());

        MvcResult mvcResult = mockMvc
                .perform(post("/grades")
                        .contentType(APPLICATION_JSON)
                        .param("grade", "85.00")
                        .param("gradeType", "math")
                        .param("studentId", "1")
                ).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");
        student = studentService.studentInformation(1);
        assertEquals(2, student.getStudentGrades().getMathGradeResults().size());
    }

    @Test
    public void createValidGradeHttpRequestStudentDoesNotExist() throws Exception {
        assertFalse(studentDao.findById(0).isPresent());

        MvcResult mvcResult = mockMvc
                .perform(post("/grades")
                        .contentType(APPLICATION_JSON)
                        .param("grade", "85.00")
                        .param("gradeType", "math")
                        .param("studentId", "0")
                ).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void createValidGradeHttpRequestInvalidSubject() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(post("/grades")
                        .contentType(APPLICATION_JSON)
                        .param("grade", "85.00")
                        .param("gradeType", "literature")
                        .param("studentId", "1")
                ).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void deleteGradeHttpRequest() throws Exception {
        Optional<MathGrade> mathGrade = mathGradeDao.findById(1);
        assertTrue(mathGrade.isPresent());
        MvcResult mvcResult = mockMvc.perform(delete("/grades/{id}/{gradeType}", 1 , "math")).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");
        assertFalse(mathGradeDao.findById(1).isPresent());
    }

    @Test
    public void deleteGradeHttpRequestGradeIdDoesNotExist() throws Exception {
        Optional<MathGrade> mathGrade = mathGradeDao.findById(0);
        assertFalse(mathGrade.isPresent());
        MvcResult mvcResult = mockMvc.perform(delete("/grades/{id}/{gradeType}", 0 , "math")).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
        assertFalse(mathGradeDao.findById(0).isPresent());
    }

    @Test
    public void deleteANonValidGradeHttpRequest() throws Exception {
        Optional<MathGrade> mathGrade = mathGradeDao.findById(1);
        assertTrue(mathGrade.isPresent());
        MvcResult mvcResult = mockMvc.perform(delete("/grades/{id}/{gradeType}", 1 , "literature")).andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
        assertTrue(mathGradeDao.findById(1).isPresent());
    }
}