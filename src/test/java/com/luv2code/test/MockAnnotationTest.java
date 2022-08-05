package com.luv2code.test;

import com.luv2code.component.MvcTestingExampleApplication;
import com.luv2code.component.dao.ApplicationDao;
import com.luv2code.component.models.CollegeStudent;
import com.luv2code.component.models.StudentGrades;
import com.luv2code.component.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class MockAnnotationTest {

    @Mock
    private ApplicationDao applicationDao;

    @InjectMocks
    private ApplicationService applicationService;

    @Autowired
    private CollegeStudent studentOne;

    @Autowired
    private StudentGrades studentGrades;

    @BeforeEach
    public void beforeEach() {
        studentOne.setFirstname("Eric");
        studentOne.setLastname("Roby");
        studentOne.setEmailAddress("eric.roby@luv2code.com");
        studentOne.setStudentGrades(studentGrades);
    }

    @Test
    @DisplayName("When & Verify")
    public void assertEqualsTestAddGraduate() {
        when(applicationDao.addGradeResultsForSingleClass(studentGrades.getMathGradeResults())).thenReturn(100.0);
        assertEquals(100.0, applicationService.addGradeResultsForSingleClass(studentGrades.getMathGradeResults()));
        verify(applicationDao).addGradeResultsForSingleClass(studentGrades.getMathGradeResults());
        verify(applicationDao, times(1)).addGradeResultsForSingleClass(studentGrades.getMathGradeResults());
    }

}
