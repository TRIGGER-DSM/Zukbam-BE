package com.example.zukbambe.domain.group.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Table(name = "tbl_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(length = 255, unique = true, nullable = false, name = "title")
    private String title;

    @Column(length = 255, name = "introduce")
    private String introduce;
}
