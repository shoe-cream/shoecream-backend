package com.springboot.member.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;  // javax.persistence 패키지에서 필요한 어노테이션을 가져옵니다.

@Entity
@Table(name = "employee")
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String employeeId;

    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;


    @Column(nullable = true, length = 15)
    private String tel;

    @Column(nullable = true)
    private String address;
}
