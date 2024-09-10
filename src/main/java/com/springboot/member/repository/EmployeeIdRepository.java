package com.springboot.member.repository;

import com.springboot.member.entity.EmployeeId;
import com.springboot.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeIdRepository extends JpaRepository<EmployeeId, Long> {
    Optional<EmployeeId> findByEmployeeId(String emplyeeId);
    boolean existsByEmployeeId(String employeeId);
}
