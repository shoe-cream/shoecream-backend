package com.springboot.order_item.repository;

import com.springboot.order_item.entity.OrderItems;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemQueryRepositoryCustom {
    //납기일이 해당 기간 내에 해당하는 주문 리스트
    List<OrderItems> findByOrderHeadersRequestDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // 해당기간의 총 판매량
    Integer findTotalOrderedByItemCd(String itemCd, LocalDateTime startDateTime, LocalDateTime endDateTime);

    //총 판매량 (승인 이후의 상태)
    Integer findTotalOrderedByItemCdAfterApproval(@Param("itemCd") String itemCd);

    //해당 기간의 총 판매액 (승인 이후의 상태)
    BigDecimal findTotalOrderPriceByItemCdAndOrderDateBetween(String itemCd, LocalDateTime startDateTime, LocalDateTime endDateTime);

    // 총 불용재고량
    Integer findTotalUnusedByItemCd(@Param("itemCd") String itemCd);

    // 총 주문 대기량
    Integer findTotalPreparationOrderByItemCd(@Param("itemCd") String itemCd);
}
