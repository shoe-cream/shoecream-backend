package com.springboot.order_header.repository;

import com.springboot.order_header.entity.OrderHeaders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderQueryRepositoryCustom {
    Page<OrderHeaders> findByRequestDateBetweenAndOrderStatus(LocalDateTime searchStartDate,
                                                              LocalDateTime searchEndDate,
                                                              OrderHeaders.OrderStatus status,
                                                              Pageable pageable);

    Page<OrderHeaders> findByRequestDateBetween(LocalDateTime startDate,
                                                LocalDateTime endDate,
                                                Pageable pageable);
}
