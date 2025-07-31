# Student API Coding Task

This task is designed to test your understanding of Java and the Spring Boot framework. You are required to implement three RESTful APIs for managing Student entities.

## Requirements

Create a Spring Boot application with the following APIs:

### 1. Create Student
- **Endpoint:** `/student/create`
- **Method:** POST
- **Description:** Create a new student record.
- **Request Body:** JSON object representing a Student:
  ```json
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "grade": "A",
    "rollNumber": "1001"
  }
  ```
- **Response:** The created Student object (with all fields).

### 2. Get All Students
- **Endpoint:** `/student/all`
- **Method:** GET
- **Description:** Retrieve a list of all students.
- **Response:** Array of Student objects.

### 3. Get Student by ID
- **Endpoint:** `/student/{id}` (e.g., `/student/3`)
- **Method:** GET
- **Description:** Retrieve a student by their unique ID.
- **Response:** The Student object with the specified ID, or an appropriate response if not found.

## Student Entity Example
The Student entity should have the following fields:
- `id` (Long)
- `name` (String)
- `email` (String)
- `grade` (String)
- `rollNumber` (String)

## Instructions
- Use standard Spring Boot practices (Controller, Service, Repository layers).
- Use in-memory database (like H2) or any database of your choice.
- Ensure proper request mapping and HTTP method usage.
- Return appropriate HTTP status codes.
- You may use Lombok for boilerplate code (optional).

## Submission
- Provide the complete source code.
- Include instructions to run the application.

---

**Good luck!** 