package com.springboot.order_header.repository;

import com.springboot.order_header.entity.OrderHeaders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHeadersRepository extends JpaRepository<OrderHeaders, Long> {
}
