package com.springboot.order_header.repository;

import com.springboot.order_header.dto.OrderDto;
import com.springboot.order_header.entity.OrderHeaders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderQueryRepositoryCustom {
    Page<OrderHeaders> findByCreatedAtBetweenAndOrderStatusAndBuyer_BuyerCdAndOrderItems_ItemCdAndOrderId(OrderDto.OrderSearchRequest orderSearchRequest,
                                                                                                          Pageable pageable);
}
