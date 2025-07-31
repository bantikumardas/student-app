package com.student.studentapp.Service;

import com.student.studentapp.Entity.Student;
import com.student.studentapp.Repo.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public ResponseEntity<Student> createStudent(Student student) {
        Map<String, String> errors = new HashMap<>();
        if(student.getName() == null || student.getName().isEmpty() || student.getEmail() == null|| student.getEmail()=="" || student.getEmail().isEmpty() || student.getGrade() == null || student.getGrade().isEmpty() || student.getRollNumber() == null || student.getRollNumber().isEmpty()) {
            errors.put("name", "Required field is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        Optional<Student> existingStudent = studentRepository.findByEmail(student.getEmail());
        if(existingStudent.isPresent()) {
            errors.put("email", "Email already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        if(existingStudent.isPresent() && existingStudent.get().getRollNumber().equals(student.getRollNumber())) {
            errors.put("rollNumber", "Roll number already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        Student savedStudent = studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.status(HttpStatus.OK).body(studentRepository.findAll());
    }

    public ResponseEntity<Student> getStudentById(Long id) {
        Map<String, String> errors = new HashMap<>();
        Optional<Student> student = studentRepository.findById(id);
        if(student.isPresent()) {
            errors.put("id", "Student not found");
            return ResponseEntity.status(HttpStatus.OK).body(errors);
        }
        return student.orElse(null);
    }
}
