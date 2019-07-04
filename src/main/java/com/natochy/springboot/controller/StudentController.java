package com.natochy.springboot.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.natochy.springboot.model.Course;
import com.natochy.springboot.model.Student;
import com.natochy.springboot.service.StudentService;

@RestController
public class StudentController {
	@Autowired
	private StudentService service;
	
	@GetMapping("/students")
	public List<Student> retrieveAllStudents(@RequestParam(value="name", required=false) String name){
		if (name == null) {
			return service.retrieveAllStudents();
		}else {
			return service.retrieveStudentsByName(name);
		}
	}
	
	@GetMapping("/students/{studentId}")
	public Student retriveStudentById(@PathVariable String studentId) {
		return service.retrieveStudent(studentId);
	}
	
	@PostMapping("/students")
	public ResponseEntity<Student> addNewStudent(@RequestBody Student newStudent){
		Student student = service.addStudent(newStudent);
		if (student == null) {
			return ResponseEntity.noContent().build();
		}
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
				"/id").buildAndExpand(student.getId()).toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	@GetMapping("/students/{studentId}/courses")
	public List<Course> retrieveCoursesForStudent(@PathVariable String studentId){
		return service.retrieveCourses(studentId);
	}
	
	@GetMapping("/students/{studentId}/courses/{courseId}")
	public Course getCourseForStudentById(@PathVariable String studentId,
			@PathVariable String courseId) {
		return service.retrieveCourse(studentId, courseId);
	}
	
	@PostMapping("/students/{studentId}/courses")
	public ResponseEntity<Void> registerStudentForCourse(
			@PathVariable String studentId, @RequestBody Course newCourse) {
		Course course = service.addCourse(studentId, newCourse);
		if (course == null) {
			//return ResponseEntity.noContent().build();
			return ResponseEntity.notFound().build();
		}
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
				"/id").buildAndExpand(course.getId()).toUri();
		
		return ResponseEntity.created(location).build();
	}
}
