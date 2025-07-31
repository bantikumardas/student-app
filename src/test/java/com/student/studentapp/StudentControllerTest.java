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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    private static String getUniqueEmail(String prefix) {
        return prefix + "." + System.currentTimeMillis() + "@example.com";
    }

    private static String getUniqueRollNumber() {
        int randomNumber = (int) (Math.random() * 1000000);
        return randomNumber+"";
    }

    @Test
    @DisplayName("Functional: Create Student - Positive Scenario")
    void testCreateStudent_Positive() throws Exception {
        // This testcase is to check the student is created successfully or not.
        String email = getUniqueEmail("alice");
        String rollNo = getUniqueRollNumber();
        
        Student student = new Student();
        student.setName("Alice");
        student.setEmail(email);
        student.setGrade("A");
        student.setRollNumber(rollNo);

        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.grade").value("A"))
                .andExpect(jsonPath("$.rollNumber").value(rollNo));
    }

    @Test
    @DisplayName("Functional: Get All Students - Positive Scenario")
    void testGetAllStudents_Positive() throws Exception {
        // Create a student first
        // This testcase cover that the all students are fetched or not.
        String email = getUniqueEmail("bob");
        String rollNo = getUniqueRollNumber();
        
        Student student = new Student();
        student.setName("Bob");
        student.setEmail(email);
        student.setGrade("B");
        student.setRollNumber(rollNo);
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
        // This testcase is to check the student is got by it or not
        String email = getUniqueEmail("charlie");
        String rollNo = getUniqueRollNumber();
        
        Student student = new Student();
        student.setName("Charlie");
        student.setEmail(email);
        student.setGrade("C");
        student.setRollNumber(rollNo);
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
                .andExpect(status().isOk())
                .andExpect(content().string("")); // Should return empty or null
    }

    @Test
    @DisplayName("Negative: Create Student with Missing Fields")
    void testCreateStudent_MissingFields() throws Exception {
        String uniqueEmail = getUniqueEmail("noName");
        
        Student student = new Student();
        student.setEmail(uniqueEmail);
        // Missing name, grade, rollNumber
        // This testcase is to check the student is not created when required fields are missing
        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Required field is missing"));
    }

    @Test
    @DisplayName("Negative: Create Student with Duplicate Email")
    void testCreateStudent_DuplicateEmail() throws Exception {
        String uniqueEmail = getUniqueEmail("john");
        String rollNo1 = getUniqueRollNumber();
        String rollNo2 = getUniqueRollNumber();
        
        // First create a student
        Student student1 = new Student();
        student1.setName("John");
        student1.setEmail(uniqueEmail);
        student1.setGrade("A");
        student1.setRollNumber(rollNo1);
        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student1)));

        // Try to create another student with same email
        Student student2 = new Student();
        student2.setName("Jane");
        student2.setEmail(uniqueEmail); // Same email
        student2.setGrade("B");
        student2.setRollNumber(rollNo2);
        mockMvc.perform(post("/student/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email already exists"));
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
        String email = getUniqueEmail("test");
        String rollNo = getUniqueRollNumber();
        
        Student student = new Student();
        student.setName("Test");
        student.setEmail(email);
        student.setGrade("A");
        student.setRollNumber(rollNo);
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