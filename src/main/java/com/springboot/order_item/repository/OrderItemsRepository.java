package com.springboot.order_item.repository;

import com.springboot.order_item.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemsRepository extends JpaRepository <OrderItems, Long> {

    @Query("SELECT oi FROM OrderItems oi " +
            "JOIN oi.orderHeaders oh " +
            "WHERE oh.requestDate BETWEEN :startDateTime AND :endDateTime")
    List<OrderItems> findByOrderHeadersRequestDateBetween(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT SUM(oi.quantity) FROM OrderItems oi " +
            "WHERE oi.itemCD = :itemCd AND oi.orderHeaders.requestDate BETWEEN :startDateTime AND :endDateTime")
    Integer findTotalOrderedByItemCD(
            @Param("itemCd") String itemCd,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT SUM(oi.quantity) FROM OrderItems oi " +
            "JOIN oi.orderHeaders oh " +
            "WHERE oi.itemCD = :itemCd AND oh.orderStatus IN ('APPROVED', 'SHIPPED', 'PRODUCT_PASS')")
    Integer findTotalOrderedByItemCdAfterApproval(@Param("itemCd") String itemCd);

    @Query("SELECT SUM(oi.unitPrice * oi.quantity) FROM OrderItems oi " +
            "JOIN oi.orderHeaders oh " +
            "WHERE oi.itemCD = :itemCd " +
            "AND oh.createdAt BETWEEN :startDate AND :endDate " +
            "AND oh.orderStatus IN ('APPROVED', 'SHIPPED', 'PRODUCT_PASS')")
    BigDecimal findTotalOrderPriceByItemCdAndOrderDateBetween(
            @Param("itemCd") String itemCd,
            @Param("startDate") LocalDateTime startDateTime,
            @Param("endDate") LocalDateTime endDateTime);
}
