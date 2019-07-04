package com.natochy.springboot.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

import com.natochy.springboot.model.Student;

public interface StudentRepository extends ElasticsearchRepository<Student, String>{
	List<Student> findByName(String name);
}
