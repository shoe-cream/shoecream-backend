package com.springboot.member.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;  // javax.persistence 패키지에서 필요한 어노테이션을 가져옵니다.

@Entity
@Table(name = "employee_id")
@Getter
@Setter
public class EmployeeId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String employeeId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;


    public enum Status {
        AVAILABLE, ASSIGNED
    }

    // 생성자, getter, setter
}
