package com.springboot.order_item.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_header.entity.QOrderHeaders;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.order_item.entity.QOrderItems;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrderItemQueryRepositoryImpl implements OrderItemQueryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public OrderItemQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<OrderItems> findByOrderHeadersRequestDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        QOrderItems orderItems = QOrderItems.orderItems;
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;

        return queryFactory
                .selectFrom(orderItems)
                .join(orderItems.orderHeaders, orderHeaders)
                .where(orderHeaders.requestDate.between(startDateTime, endDateTime))
                .fetch();
    }

    @Override
    public Integer findTotalOrderedByItemCd(String itemCd, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        QOrderItems orderItems = QOrderItems.orderItems;
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;

        return queryFactory
                .select(orderItems.qty.sum())
                .from(orderItems)
                .where(orderItems.itemCd.eq(itemCd)
                        .and(orderHeaders.requestDate.between(startDateTime, endDateTime)))
                .fetchOne();
    }

    @Override
    public Integer findTotalOrderedByItemCdAfterApproval(String itemCd) {
        QOrderItems orderItems = QOrderItems.orderItems;
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;

        return queryFactory
                .select(orderItems.qty.sum())
                .from(orderItems)
                .join(orderItems.orderHeaders, orderHeaders)
                .where(orderItems.itemCd.eq(itemCd)
                        .and(orderHeaders.orderStatus.in(OrderHeaders.OrderStatus.APPROVED,
                                OrderHeaders.OrderStatus.SHIPPED,
                                OrderHeaders.OrderStatus.PRODUCT_PASS)))
                .fetchOne();
    }

    @Override
    public BigDecimal findTotalOrderPriceByItemCdAndOrderDateBetween(String itemCd, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        QOrderItems orderItems = QOrderItems.orderItems;
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;

        return queryFactory
                .select(orderItems.unitPrice.sum())
                .from(orderItems)
                .join(orderItems.orderHeaders, orderHeaders)
                .where(orderItems.itemCd.eq(itemCd)
                        .and(orderHeaders.orderStatus.eq(OrderHeaders.OrderStatus.PRODUCT_PASS)))
                .fetchOne();
    }

    @Override
    public Integer findTotalUnusedByItemCd(String itemCd) {
        QOrderItems orderItems = QOrderItems.orderItems;
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;

        return queryFactory
                .select(orderItems.qty.sum())
                .from(orderItems)
                .join(orderItems.orderHeaders, orderHeaders)
                .where(orderItems.itemCd.eq(itemCd)
                        .and(orderHeaders.orderStatus.eq(OrderHeaders.OrderStatus.PRODUCT_FAIL)))
                .fetchOne();
    }

    @Override
    public Integer findTotalPreparationOrderByItemCd(String itemCd) {
        QOrderItems orderItems = QOrderItems.orderItems;
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;

        return queryFactory
                .select(orderItems.qty.sum())
                .from(orderItems)
                .join(orderItems.orderHeaders, orderHeaders)
                .where(orderItems.itemCd.eq(itemCd)
                        .and(orderHeaders.orderStatus.in(OrderHeaders.OrderStatus.PURCHASE_REQUEST,
                                OrderHeaders.OrderStatus.REQUEST_TEMP)))
                .fetchOne();
    }
}
