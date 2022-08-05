package com.luv2code.test;

import com.luv2code.component.MvcTestingExampleApplication;
import com.luv2code.component.dao.ApplicationDao;
import com.luv2code.component.models.CollegeStudent;
import com.luv2code.component.models.StudentGrades;
import com.luv2code.component.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class MockAnnotation2Test {

    @MockBean
    private ApplicationDao applicationDao;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationContext applicationContext;

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

    @Test
    @DisplayName("Find GPA")
    public void assertEqualsTestFindGpa() {
        when(applicationDao.findGradePointAverage(studentGrades.getMathGradeResults())).thenReturn(88.31);
        assertEquals(88.31, applicationService.findGradePointAverage(studentGrades.getMathGradeResults()));
        verify(applicationDao).findGradePointAverage(studentGrades.getMathGradeResults());
        verify(applicationDao, times(1)).findGradePointAverage(studentGrades.getMathGradeResults());
    }

    @Test
    @DisplayName("Not Null")
    public void testAssertNotNull() {
        when(applicationDao.checkNull(studentOne.getStudentGrades())).thenReturn(true);
        assertNotNull( applicationService.checkNull(studentOne.getStudentGrades()));
        verify(applicationDao, times(1)).checkNull(studentOne.getStudentGrades());
    }

    @Test
    @DisplayName("Throw an Exception")
    public void throwAnException() {
        CollegeStudent nullStudent = applicationContext.getBean(CollegeStudent.class);
        when(applicationDao.checkNull(nullStudent)).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> applicationService.checkNull(nullStudent));
        verify(applicationDao, times(1)).checkNull(nullStudent);
    }

    @Test
    @DisplayName("Throw RuntimeException")
    public void throwRuntimeException() {
        CollegeStudent nullStudent = applicationContext.getBean(CollegeStudent.class);
        doThrow(new RuntimeException()).when(applicationDao).checkNull(nullStudent);
        assertThrows(RuntimeException.class, () -> applicationService.checkNull(nullStudent));
        verify(applicationDao, times(1)).checkNull(nullStudent);
    }

    @Test
    @DisplayName("Multiple Stubbing")
    public void stubbingConsecutiveCalls() {
        CollegeStudent nullStudent = applicationContext.getBean(CollegeStudent.class);
        when(applicationDao.checkNull(nullStudent))
                .thenThrow(new RuntimeException())
                .thenReturn("Do not throw exception second time");
        assertThrows(RuntimeException.class, () -> applicationService.checkNull(nullStudent));
        assertEquals("Do not throw exception second time", applicationService.checkNull(nullStudent));
        verify(applicationDao, times(2)).checkNull(nullStudent);
    }

}
