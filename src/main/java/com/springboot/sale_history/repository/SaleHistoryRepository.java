package com.springboot.sale_history.repository;

import com.springboot.sale_history.entity.SaleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleHistoryRepository extends JpaRepository <SaleHistory, Long> {
    List<SaleHistory> findByOrderHeaders_OrderId(Long orderId);
}
