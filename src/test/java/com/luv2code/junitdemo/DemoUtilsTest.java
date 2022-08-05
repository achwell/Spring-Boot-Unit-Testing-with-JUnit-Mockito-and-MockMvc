package com.luv2code.junitdemo;

import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DemoUtilsTest {

    private DemoUtils testSubject;

    @BeforeEach
    void setUp() {
        testSubject = new DemoUtils();
        System.out.println("@BeforeEach executes before the execution of each test method");
    }

    @AfterEach
    void tearDownAfterEach() {
        System.out.println("Running @AfterEach");
    }

    @BeforeAll
    static void setupBeforeEachClass() {
        System.out.println("@BeforeAll executes only once before all test methods execution in the class");
    }

    @AfterAll
    static void tearDownAfterAll() {
        System.out.println("@AfterAll executes only once after all test methods execution in the class");
    }

    @Test
    @DisplayName("Equals and Not Equals")
    @Order(1)
    void testEqualsAndNotEquals() {
        assertAll(
                "add",
                () -> assertEquals(6, testSubject.add(2, 4), "2+4 must be 6"),
                () -> assertNotEquals(6, testSubject.add(1, 9), "1+9 must not be 6")
        );
    }

    @Test
    @DisplayName("Null and Not Null")
    @Order(0)
    void testNullAndNotNull() {
        String str1 = null;
        String str2 = "luv2code";

        assertAll(
                "checkNull",
                () -> assertNull(testSubject.checkNull(str1), "Object should be null"),
                () -> assertNotNull(testSubject.checkNull(str2), "Object should not be null")
        );
    }

    @Test
    @DisplayName("Same and Not Same")
    void testSameAndNotSame() {
        String str = "luv2code";
        assertAll(
                "getAcademy + getAcademyDuplicate",
                () -> assertSame(testSubject.getAcademy(), testSubject.getAcademyDuplicate(), "Objects should refer to same object"),
                () -> assertNotSame(str, testSubject.getAcademy(), "Objects should not refer to same object")
        );
    }

    @Test
    @DisplayName("True and False")
    @Order(30)
    void testTrueFalse() {
        int gradeOne = 10;
        int gradeTwo = 5;
        assertAll(
                "isGreater",
                () -> assertTrue(testSubject.isGreater(gradeOne, gradeTwo), "This should return true"),
                () -> assertFalse(testSubject.isGreater(gradeTwo, gradeOne), "This should return false")
        );
    }

    @Test
    @DisplayName("Array Equals")
    void testArrayEquals() {
        String[] stringArray = {"A", "B", "C"};
        assertArrayEquals(stringArray, testSubject.getFirstThreeLettersOfAlphabet(), "Arrays should be the same");
    }

    @Test
    @DisplayName("Iterable equals")
    void testIterableEquals() {
        List<String> theList = List.of("luv", "2", "code");
        assertIterableEquals(theList, testSubject.getAcademyInList(), "Expected list should be same as actual list");
    }

    @Test
    @DisplayName("Lines match")
    @Order(50)
    void testLinesMatch() {
        List<String> theList = List.of("luv", "2", "code");
        assertLinesMatch(theList, testSubject.getAcademyInList(), "Lines should match");
    }

    @Test
    @DisplayName("Throws and Does Not Throw")
    void testThrowsAndDoesNotThrow() {
        assertAll(
                "throwException",
                () -> assertThrows(Exception.class, () -> { testSubject.throwException(-1); }, "Should throw exception"),
                () -> assertDoesNotThrow(() -> { testSubject.throwException(5); }, "Should not throw exception")
        );
    }

    @Test
    @DisplayName("Timeout")
    void testTimeout() {
        assertTimeoutPreemptively(Duration.ofSeconds(3), () -> { testSubject.checkTimeout(); },
                "Method should execute in 3 seconds");
    }

    @DisplayName("Multiply")
    @Test
    void testMultiply() {
        assertEquals(12, testSubject.multiply(4, 3), "4*3 must be 12");
    }
}