package com.springboot.sale_history.repository;

import com.springboot.sale_history.entity.SaleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleHistoryRepository extends JpaRepository <SaleHistory, Long> {
}
