package com.example.zukbambe.domain.student.domain;

import com.example.zukbambe.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_students")
public class Student {

    @Id
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, name = "grade")
    private Short grade;

    @Column(nullable = false, name = "class_number")
    private Short classNumber;

    @Column(nullable = false, name = "student_number")
    private Short studentNumber;
}
