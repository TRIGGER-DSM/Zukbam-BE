package com.example.zukbambe.domain.student.domain.repository;

import com.example.zukbambe.domain.student.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
