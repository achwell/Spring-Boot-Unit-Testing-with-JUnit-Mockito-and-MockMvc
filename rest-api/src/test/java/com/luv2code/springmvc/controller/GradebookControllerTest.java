package com.luv2code.springmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.HistoryGrade;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.ScienceGrade;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Transactional
class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @PersistenceContext
    private EntityManager entityManager;

    @Mock
    StudentAndGradeService studentAndGradeServiceMock;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradesDao mathGradeDao;

    @Autowired
    private ScienceGradesDao scienceGradeDao;

    @Autowired
    private HistoryGradesDao historyGradeDao;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public static final MediaType APPLICATION_JSON_UTF8 = MediaType.APPLICATION_JSON;

    @BeforeAll
    public static void setup() {
        request = new MockHttpServletRequest();
        request.setParameter("firstName", "Chad");
        request.setParameter("lastName", "Darby");
        request.setParameter("emailAddress", "chad.darby@luv2code_school.com");
    }

    @BeforeEach
    public void setupDatabase() {
        studentDao.save(new CollegeStudent("Eric", "Roby", "eric.roby@luv2code_school.com"));
        mathGradeDao.save(new MathGrade(100.0, 1));
        scienceGradeDao.save(new ScienceGrade(100.0, 1));
        historyGradeDao.save(new HistoryGrade(100.0, 1));
    }

    @Test
    void getStudentsHttpRequest() throws Exception {
        studentDao.save(new CollegeStudent("Chad", "Darby", "chad.darby@luv2code_school.com"));
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void createStudentHttpRequest() throws Exception {
        CollegeStudent student = new CollegeStudent("Chad", "Darby", "chad.darby@luv2code_school.com");
        mockMvc.perform(
                        post("/")
                                .contentType(APPLICATION_JSON_UTF8)
                                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)));

        CollegeStudent verifyStudent = studentDao.findByEmailAddress("chad.darby@luv2code_school.com");
        assertNotNull(verifyStudent, "Student should be valid");
    }

    @Test
    void deleteStudentHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());
        mockMvc.perform(delete("/student/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(0)));
        assertTrue(studentDao.findById(1).isEmpty());
    }

    @Test
    void deleteStudentHttpRequestErrorPage() throws Exception {
        assertTrue(studentDao.findById(0).isEmpty());
        mockMvc.perform(delete("/student/{id}", 0))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
        assertTrue(studentDao.findById(0).isEmpty());
    }

    @Test
    void studentInformationHttpRequest() throws Exception {
        Optional<CollegeStudent> student = studentDao.findById(1);
        assertTrue(student.isPresent());
        mockMvc.perform(get("/studentInformation/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Eric")))
                .andExpect(jsonPath("$.lastname", is("Roby")))
                .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")));
    }

    @Test
    void studentInformationHttpRequestEmptyResponse() throws Exception {
        Optional<CollegeStudent> student = studentDao.findById(0);
        assertTrue(student.isEmpty());
        mockMvc.perform(get("/studentInformation/{id}", 0))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    void createGradeHttpRequest() throws Exception {
        mockMvc.perform(
                        post("/grades")
                                .contentType(APPLICATION_JSON_UTF8)
                                .param("grade", "85.00")
                                .param("gradeType", "math")
                                .param("studentId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Eric")))
                .andExpect(jsonPath("$.lastname", is("Roby")))
                .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")))
                .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(2)));
    }

    @Test
    void createGradeHttpRequestStudentDoesNotExist() throws Exception {
        mockMvc.perform(
                        post("/grades")
                                .contentType(APPLICATION_JSON_UTF8)
                                .param("grade", "85.00")
                                .param("gradeType", "math")
                                .param("studentId", "0"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    void createGradeHttpRequestGradeTypeDoesNotExist() throws Exception {
        mockMvc.perform(
                        post("/grades")
                                .contentType(APPLICATION_JSON_UTF8)
                                .param("grade", "85.00")
                                .param("gradeType", "literature")
                                .param("studentId", "1"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }

    @Test
    void deleteGradeHttpRequest() throws Exception {
        assertTrue(mathGradeDao.findById(1).isPresent());
        mockMvc.perform(delete("/grades/{id}/{gradeType}", 1, "math"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Eric")))
                .andExpect(jsonPath("$.lastname", is("Roby")))
                .andExpect(jsonPath("$.emailAddress", is("eric.roby@luv2code_school.com")))
                .andExpect(jsonPath("$.studentGrades.mathGradeResults", hasSize(0)));
        assertTrue(mathGradeDao.findById(1).isEmpty());
    }

    @Test
    void deleteGradeHttpRequestStudentIdDoesNotExist() throws Exception {
        assertTrue(historyGradeDao.findById(2).isEmpty());
        mockMvc.perform(delete("/grades/{id}/{gradeType}", 2, "history"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
        assertTrue(historyGradeDao.findById(2).isEmpty());
    }

    @Test
    void deleteGradeHttpRequestInvalidGrade() throws Exception {
        mockMvc.perform(delete("/grades/{id}/{gradeType}", 1, "literature"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Student or Grade was not found")));
    }
}