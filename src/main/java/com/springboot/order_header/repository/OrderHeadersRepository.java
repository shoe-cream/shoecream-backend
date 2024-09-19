package com.springboot.order_header.repository;

import com.springboot.order_header.entity.OrderHeaders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderHeadersRepository extends JpaRepository<OrderHeaders, Long> {
    Optional<OrderHeaders> findByOrderCd(String orderCd);
}
