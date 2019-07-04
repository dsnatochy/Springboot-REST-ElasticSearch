package com.natochy.springboot.service;

import java.math.BigInteger;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.natochy.springboot.model.Course;
import com.natochy.springboot.model.Student;
import com.natochy.springboot.repository.StudentRepository;

@Repository
public class StudentService {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Value("${elasticsearch.index.name}")
	private String indexName;
	
	@Value("${elasticsearch.user.type}")
	private String userType;
	
	@Autowired
	private ElasticsearchTemplate esTemplate;
	
	private static List<Student> students = new ArrayList<>();
	
	static {
		//Initialize Data
		Course course1 = new Course("Course1", "Spring", "10 Steps", Arrays
				.asList("Learn Maven", "Import Project", "First Example",
						"Second Example"));
		Course course2 = new Course("Course2", "Spring MVC", "10 Examples",
				Arrays.asList("Learn Maven", "Import Project", "First Example",
						"Second Example"));
		Course course3 = new Course("Course3", "Spring Boot", "6K Students",
				Arrays.asList("Learn Maven", "Learn Spring",
						"Learn Spring MVC", "First Example", "Second Example"));
		Course course4 = new Course("Course4", "Maven",
				"Most popular maven course on internet!", Arrays.asList(
						"Pom.xml", "Build Life Cycle", "Parent POM",
						"Importing into Eclipse"));

		Student ranga = new Student("Student1", "Ranga Karanam",
				"Hiker, Programmer and Architect", new ArrayList<>(Arrays
						.asList(course1, course2, course3, course4)));

		Student satish = new Student("Student2", "Satish T",
				"Hiker, Programmer and Architect", new ArrayList<>(Arrays
						.asList(course1, course2, course3, course4)));

		students.add(ranga);
		students.add(satish);
	}
	
	/*
	 * private StudentRepository studentRepository;
	 * 
	 * @Autowired public void setStudentRepository(StudentRepository
	 * studentRepository) { this.studentRepository = studentRepository; }
	 */
	public List<Student> retrieveAllStudents() {
		//return students;
		SearchQuery getAllQuery = new NativeSearchQueryBuilder()
				.withQuery(matchAllQuery()).build();
		return esTemplate.queryForList(getAllQuery, Student.class);
	}
	
	public List<Student> retrieveStudentsByName(String name){
		SearchQuery query = new NativeSearchQueryBuilder()
				.withQuery(QueryBuilders.matchQuery("name", name)).build();
		
		List<Student> students = esTemplate.queryForList(query, Student.class);
		return students;
	}
	
	public Student retrieveStudent(String studentId) {
		for (Student student : students) {
			if (student.getId().equals(studentId)) {
				return student;
			}
		}
		return null;
	}
	
	public List<Course> retrieveCourses(String studentId) {
		Student student = retrieveStudent(studentId);

		if (student == null) {
			return null;
		}

		return student.getCourses();
	}
	
	public Course retrieveCourse(String studentId, String courseId) {
		Student student = retrieveStudent(studentId);
		
		if (student != null) {
			List<Course> courses = student.getCourses();
			for (Course course : courses) {
				if (course.getId().equals(courseId)) {
					return course;
				}
			}
		}
		return null;
	}
	
	private SecureRandom random = new SecureRandom();
	
	public Course addCourse(String studentId, Course course) {
		Student student = retrieveStudent(studentId);
		if (student == null) {
			return null;
		}
		String randomId = new BigInteger(130, random).toString(32);
		course.setId(randomId);
		student.getCourses().add(course);
		return course;

	}
	
	public Student addStudent(Student student) {
		IndexQuery studentQuery = new IndexQuery();
		studentQuery.setIndexName(indexName);
		studentQuery.setType(userType);
		studentQuery.setObject(student);
		studentQuery.setId(UUID.randomUUID().toString());
		
		LOG.info("Student indexed: {}", esTemplate.index(studentQuery));
	
		esTemplate.refresh(indexName);
		return student;
	}
	
	
}
