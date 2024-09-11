package com.springboot.order_item.repository;

import com.springboot.order_item.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemsRepository extends JpaRepository <OrderItems, Long> {

    //납기일이 해당 기간 내에 해당하는 주문 리스트
    @Query("SELECT oi FROM OrderItems oi " +
            "JOIN oi.orderHeaders oh " +
            "WHERE oh.requestDate BETWEEN :startDateTime AND :endDateTime")
    List<OrderItems> findByOrderHeadersRequestDateBetween(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    // 해당기간의 총 판매량
    @Query("SELECT SUM(oi.qty) FROM OrderItems oi " +
            "WHERE oi.itemCd = :itemCd AND oi.orderHeaders.requestDate BETWEEN :startDateTime AND :endDateTime")
    Integer findTotalOrderedByItemCd(
            @Param("itemCd") String itemCd,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    //총 판매량 (승인 이후의 상태)
    @Query("SELECT SUM(oi.qty) FROM OrderItems oi " +
            "JOIN oi.orderHeaders oh " +
            "WHERE oi.itemCd = :itemCd AND oh.orderStatus IN ('APPROVED', 'SHIPPED', 'PRODUCT_PASS')")
    Integer findTotalOrderedByItemCdAfterApproval(@Param("itemCd") String itemCd);


    //해당 기간의 총 판매액 (승인 이후의 상태)
    @Query("SELECT SUM(oi.unitPrice * oi.qty) FROM OrderItems oi " +
            "JOIN oi.orderHeaders oh " +
            "WHERE oi.itemCd = :itemCd " +
            "AND oh.createdAt BETWEEN :startDate AND :endDate " +
            "AND oh.orderStatus IN ('APPROVED', 'SHIPPED', 'PRODUCT_PASS')")
    BigDecimal findTotalOrderPriceByItemCdAndOrderDateBetween(
            @Param("itemCd") String itemCd,
            @Param("startDate") LocalDateTime startDateTime,
            @Param("endDate") LocalDateTime endDateTime);

    // 총 불용재고량
    @Query("SELECT SUM(oi.qty) FROM OrderItems oi " +
            "JOIN oi.orderHeaders oh " +
            "WHERE oi.itemCd = :itemCd AND oh.orderStatus = 'PRODUCT_FAIL'")
    Integer findTotalUnusedByItemCd(@Param("itemCd") String itemCd);

    // 총 주문 대기량
    @Query("SELECT SUM(oi.qty) FROM OrderItems oi " +
            "JOIN oi.orderHeaders oh " +
            "WHERE oi.itemCd = :itemCd AND oh.orderStatus IN ('REQUEST_TEMP', 'PURCHASE_REQUEST')")
    Integer findTotalPreparationOrderByItemCd(@Param("itemCd") String itemCd);

}
