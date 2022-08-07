package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.Gradebook;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

    @Autowired
    private Gradebook gradebook;

    @Autowired
    private StudentAndGradeService studentService;


    @GetMapping
    public String getStudents(Model m) {
        m.addAttribute("students", studentService.getGradebook());
        return "index";
    }

    @GetMapping("/studentInformation/{id}")
    public String studentInformation(@PathVariable int id, Model m) {
        if (studentService.checkIfStudentIsNull(id)) {
            return "error";
        }
        studentService.configureStudentInformationModel(id, m);
        return "studentInformation";
    }

    @PostMapping
    public String createStudent(@ModelAttribute("student") CollegeStudent student, Model m) {
        studentService.createStudent(student.getFirstname(), student.getLastname(), student.getEmailAddress());
        m.addAttribute("students", studentService.getGradebook());
        return "index";
    }

    @DeleteMapping("/delete/student/{id}")
    public String deleteStudent(@PathVariable Integer id, Model m) {
        if (studentService.checkIfStudentIsNull(id)) {
            return "error";
        }
        studentService.delete(id);
        m.addAttribute("students", studentService.getGradebook());
        return "index";
    }


    @PostMapping("/grades")
    public String createGrade(@RequestParam("grade") double grade, @RequestParam("gradeType") String gradeType, @RequestParam("studentId") int studentId, Model m) {
        if (studentService.checkIfStudentIsNull(studentId)) {
            return "error";
        }
        boolean success = studentService.createGrade(grade, studentId, gradeType);
        if (!success) {
            return "error";
        }
        studentService.configureStudentInformationModel(studentId, m);
        return "studentInformation";
    }

    @DeleteMapping("/grades/{id}/{gradeType}")
    public String deleteGrade(@PathVariable Integer id, @PathVariable String gradeType, Model m) {
        int studentId = studentService.deleteGrade(id, gradeType);
        if (studentId == 0) {
            return "error";
        }
        studentService.configureStudentInformationModel(studentId, m);
        return "studentInformation";
    }

}
