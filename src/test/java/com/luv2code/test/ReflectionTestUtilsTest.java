package com.luv2code.test;

import com.luv2code.component.MvcTestingExampleApplication;
import com.luv2code.component.models.CollegeStudent;
import com.luv2code.component.models.StudentGrades;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class ReflectionTestUtilsTest {

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

        ReflectionTestUtils.setField(studentOne, "id", 1);
        ReflectionTestUtils.setField(studentOne, "studentGrades", new StudentGrades(asList(100.0, 85.0, 76.50, 91.75)));
    }

    @Test
    @DisplayName("Get Private Field")
    public void getPrivateField() {
        assertEquals(1, ReflectionTestUtils.getField(studentOne, "id"));
    }

    @Test
    @DisplayName("Invoke Private Method")
    public void invokePrivateMethod() {
        assertEquals("Eric 1", ReflectionTestUtils.invokeMethod(studentOne, "getFirstNameAndId"), "Fail private method not call");
    }

}
