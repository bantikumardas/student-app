package com.student.studentapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.studentapp.Entity.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Functional: Create Student - Positive Scenario")
    void testCreateStudent_Positive() throws Exception {
        // This testcase is to check the student is created successfully or not.
        Student student = new Student();
        student.setName("Alice");
        student.setEmail("alice@example.com");
        student.setGrade("A");
        student.setRollNumber("101");

        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.grade").value("A"))
                .andExpect(jsonPath("$.rollNumber").value("101"));
    }

    @Test
    @DisplayName("Functional: Get All Students - Positive Scenario")
    void testGetAllStudents_Positive() throws Exception {
        // Create a student first
        // This testcase cover that the all students are fetched or not.
        Student student = new Student();
        student.setName("Bob");
        student.setEmail("bob@example.com");
        student.setGrade("B");
        student.setRollNumber("102");
        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)));

        mockMvc.perform(get("/student/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", notNullValue()));
    }

    @Test
    @DisplayName("Functional: Get Student by ID - Positive Scenario")
    void testGetStudentById_Positive() throws Exception {
        // Create a student first
        // This testcase is to check the student is get by it or not
        Student student = new Student();
        student.setName("Charlie");
        student.setEmail("charlie@example.com");
        student.setGrade("C");
        student.setRollNumber("103");
        String response = mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andReturn().getResponse().getContentAsString();
        Student created = objectMapper.readValue(response, Student.class);

        mockMvc.perform(get("/student/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Charlie"));
    }

    @Test
    @DisplayName("Negative: Get Student by Invalid ID")
    void testGetStudentById_Negative() throws Exception {
        // This testcase is to check the student is not found by invalid id
        mockMvc.perform(get("/student/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.id").value("Student not found"));
    }

    @Test
    @DisplayName("Negative: Create Student with Missing Fields")
    void testCreateStudent_MissingFields() throws Exception {
        Student student = new Student();
        student.setEmail("noName@example.com");
        // Missing name, grade, rollNumber
        // This testcase is to check the student is not created when required fields are missing
        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is mandatory"))
                .andExpect(jsonPath("$.grade").value("Grade is mandatory"))
                .andExpect(jsonPath("$.rollNumber").value("Roll number is mandatory"));
    }

    @Test
    @DisplayName("Non-Functional: Response Time for Get All Students")
    void testGetAllStudents_ResponseTime() throws Exception {
        long start = System.currentTimeMillis();
        mockMvc.perform(get("/student/all"))
                .andExpect(status().isOk());
        long duration = System.currentTimeMillis() - start;
        // Assert response time is under 2 seconds
        assert(duration < 2000);
    }

    @Test
    @DisplayName("Non-Functional: Content Type for All APIs")
    void testContentType_AllEndpoints() throws Exception {
        Student student = new Student();
        student.setName("Test");
        student.setEmail("test@example.com");
        student.setGrade("A");
        student.setRollNumber("104");
        // Create
        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        // Get All
        mockMvc.perform(get("/student/all"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
} 