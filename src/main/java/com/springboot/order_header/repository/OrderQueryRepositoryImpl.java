package com.springboot.order_header.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.buyer.entity.QBuyer;
import com.springboot.order_header.dto.OrderDto;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_header.entity.QOrderHeaders;
import com.springboot.order_item.entity.QOrderItems;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrderQueryRepositoryImpl implements OrderQueryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrderQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<OrderHeaders> findByCreatedAtBetweenAndOrderStatusAndBuyer_BuyerCdAndOrderItems_ItemCdAndOrderCd(OrderDto.OrderSearchRequest orderSearchRequest,
                                                                                                                 Pageable pageable) {

        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;
        QBuyer buyer = QBuyer.buyer;
        QOrderItems orderItems = QOrderItems.orderItems;

        BooleanBuilder builder = new BooleanBuilder();

        // 날짜 필터
        if (orderSearchRequest.getSearchStartDate() != null && orderSearchRequest.getSearchEndDate() != null) {
            LocalDateTime searchStartDate = orderSearchRequest.getSearchStartDate().atStartOfDay();
            LocalDateTime searchEndDate = orderSearchRequest.getSearchEndDate().atTime(23, 59, 59);
            builder.and(orderHeaders.requestDate.between(searchStartDate, searchEndDate));
        }

        // 상태 필터
        if (orderSearchRequest.getStatus() != null) {
            builder.and(orderHeaders.orderStatus.eq(orderSearchRequest.getStatus()));
        }

        // BuyerCode 필터
        if (orderSearchRequest.getBuyerCd() != null && !orderSearchRequest.getBuyerCd().trim().isEmpty()) {
            builder.and(buyer.buyerCd.eq(orderSearchRequest.getBuyerCd()));
        }

        // ItemCode 필터
        if (orderSearchRequest.getItemCd() != null && !orderSearchRequest.getItemCd().trim().isEmpty()) {
            builder.and(orderItems.itemCd.eq(orderSearchRequest.getItemCd()));
        }

        //orderCode 필터
        if (orderSearchRequest.getOrderCd() != null) {
            builder.and(orderHeaders.orderCd.eq(orderSearchRequest.getOrderCd()));
        }

        List<OrderHeaders> results = queryFactory
                .selectDistinct(orderHeaders)  // DISTINCT 사용
                .from(orderHeaders)
                .leftJoin(orderHeaders.buyer, buyer)
                .leftJoin(orderHeaders.orderItems, orderItems).fetchJoin()  // fetchJoin 사용
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderHeaders.createdAt.desc())
                .fetch();

        long total = queryFactory
                .select(orderHeaders.countDistinct())
                .from(orderHeaders)
                .leftJoin(orderHeaders.buyer, buyer)
                .leftJoin(orderHeaders.orderItems, orderItems)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
