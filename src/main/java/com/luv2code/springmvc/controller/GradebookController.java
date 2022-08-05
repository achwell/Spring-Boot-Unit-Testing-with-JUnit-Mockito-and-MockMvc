package com.luv2code.springmvc.controller;

import com.luv2code.springmvc.models.*;
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
        if(studentService.checkIfStudentIsNull(id)) {
            return "error";
        }
        studentService.delete(id);
        m.addAttribute("students", studentService.getGradebook());
        return "index";
    }
}
