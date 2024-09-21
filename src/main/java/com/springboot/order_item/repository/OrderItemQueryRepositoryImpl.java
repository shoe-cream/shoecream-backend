package com.springboot.order_item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import com.springboot.member.entity.QMember;
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


    //해당 기간 내 판매제품 리스트
    @Override
    public List<OrderItems> findByOrderHeadersRequestDateBetween(String employeeId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        QOrderItems orderItems = QOrderItems.orderItems;
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;
        QMember member = QMember.member;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if(employeeId != null && !employeeId.isEmpty()) {
            booleanBuilder.and(member.employeeId.eq(employeeId));
        }

        booleanBuilder.and(orderHeaders.requestDate.between(startDateTime, endDateTime));

        return queryFactory
                .selectFrom(orderItems)
                .join(orderItems.orderHeaders, orderHeaders)
                .join(orderHeaders.member, member)
                .where(booleanBuilder)
                .fetch();
    }

    //선택기간 동안의 총 주문량
    @Override
    public Integer findTotalOrderedByItemCd(String itemCd, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        QOrderItems orderItems = QOrderItems.orderItems;
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;

        return queryFactory
                .select(orderItems.qty.sum())
                .from(orderItems)
                .where(orderItems.itemCd.eq(itemCd)
                        .and(orderHeaders.requestDate.between(startDateTime, endDateTime))
                        .and(orderHeaders.orderStatus.in(OrderHeaders.OrderStatus.APPROVED,
                                OrderHeaders.OrderStatus.PRODUCT_PASS)))
                .fetchOne();
    }

    //총 판매량 (재고계산용)
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
                                OrderHeaders.OrderStatus.PRODUCT_PASS)))
                .fetchOne();
    }

    //해당 기간의 총 판매액 (합격 상태)
    @Override
    public BigDecimal findTotalOrderPriceByItemCdAndOrderDateBetween(String itemCd, String employeeId, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        QOrderItems orderItems = QOrderItems.orderItems;
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder();

        if(itemCd !=null && !itemCd.isEmpty()) {
            builder.and(orderItems.itemCd.eq(itemCd));
        }

        if(employeeId != null && !employeeId.isEmpty()) {
            builder.and(orderItems.orderHeaders.member.employeeId.eq(employeeId));
        }

        if(startDateTime != null && endDateTime != null) {
            builder.and(orderItems.orderHeaders.requestDate.between(startDateTime, endDateTime));
        }

        builder.and(orderHeaders.orderStatus.eq(OrderHeaders.OrderStatus.PRODUCT_PASS));


        return queryFactory
                .select(orderItems.unitPrice.sum())
                .from(orderItems)
                .join(orderItems.orderHeaders, orderHeaders)
                .where(builder)
                .fetchOne();
    }

    //불용재고 수량
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

    //발주 대기수량
    @Override
    public Integer findTotalPreparationOrderByItemCd(String itemCd) {
        QOrderItems orderItems = QOrderItems.orderItems;
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;

        return queryFactory
                .select(orderItems.qty.sum())
                .from(orderItems)
                .join(orderItems.orderHeaders, orderHeaders)
                .where(orderItems.itemCd.eq(itemCd)
                        .and(orderHeaders.orderStatus.eq(OrderHeaders.OrderStatus.REQUEST_TEMP)))
                .fetchOne();
    }
}
