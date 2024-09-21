package com.springboot.order_header.repository;

import com.springboot.order_header.dto.OrderDto;
import com.springboot.order_header.entity.OrderHeaders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderQueryRepositoryCustom {
    Page<OrderHeaders> findByCreatedAtBetweenAndOrderStatusAndBuyer_BuyerCdAndOrderItems_ItemCdAndOrderCd(OrderDto.OrderSearchRequest orderSearchRequest,
                                                                                                          Pageable pageable);

    //해당 기간동안 판매 건수
    Integer getOrderCountByEmployee(String employeeId, LocalDateTime start, LocalDateTime end);
}
