package com.springboot.order_header.repository;

import com.springboot.order_header.dto.OrderDto;
import com.springboot.order_header.entity.OrderHeaders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderQueryRepositoryCustom {
    Page<OrderHeaders> findByRequestDateBetweenAndOrderStatusAndBuyer_BuyerCdAndOrderItems_ItemCD(OrderDto.OrderSearchRequest orderSearchRequest,
                                                                                                  Pageable pageable);
}
