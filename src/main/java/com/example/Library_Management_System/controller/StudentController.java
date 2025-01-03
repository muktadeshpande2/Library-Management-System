package com.example.Library_Management_System.controller;

import com.example.Library_Management_System.dto.*;
import com.example.Library_Management_System.model.Student;
import com.example.Library_Management_System.model.User;
import com.example.Library_Management_System.service.StudentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentService studentService;


    @PostMapping("/add")
    public ResponseEntity<String> addOrUpdateStudent(@RequestBody @Valid AddStudentRequest addStudentRequest) {

        try{
            studentService.addStudentOrUpdate(addStudentRequest.to());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("Student Added Successfully");

        } catch(Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }

    }


    @GetMapping("/search")
    public SearchStudentResponse searchStudent(@RequestBody @Valid SearchStudentRequest searchStudentRequest){
        try {
            List<Student> studentList = studentService.searchStudent(searchStudentRequest.getSearchKey(), searchStudentRequest.getSearchValue());
            List<StudentResponse> studentResponseList = searchStudentRequest.createResponse(studentList);
            return new SearchStudentResponse(studentResponseList);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new SearchStudentResponse();
        }

    }

    @GetMapping("/info")
    public ResponseEntity<StudentResponse> getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Integer id = user.getStudent().getId();

        try{
            List<Student> studentList = studentService.searchStudent("id", String.valueOf(id));

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(studentList.getFirst().to());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(StudentResponse.createErrorResponse(e.getMessage()));
        }
    }
}
