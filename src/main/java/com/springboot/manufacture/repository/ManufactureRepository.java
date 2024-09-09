package com.springboot.manufacture.repository;

import com.springboot.manufacture.entity.Manufacture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufactureRepository extends JpaRepository<Manufacture, Long> {
}
