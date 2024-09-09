package com.springboot.sale_history.repository;

import com.springboot.sale_history.entity.SaleHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleHistoryRepository extends JpaRepository <SaleHistory, Long> {
    Page<SaleHistory> findByOrderHeaders_OrderId(Long orderId, Pageable pageable);
}
