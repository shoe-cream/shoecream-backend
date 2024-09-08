package com.springboot.order_header.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_header.entity.QOrderHeaders;
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
    public Page<OrderHeaders> findByRequestDateBetweenAndOrderStatus(LocalDateTime searchStartDate, LocalDateTime searchEndDate, OrderHeaders.OrderStatus status, Pageable pageable) {
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(orderHeaders.requestDate.between(searchStartDate, searchEndDate));

        builder.and(orderHeaders.orderStatus.eq(status));


        List<OrderHeaders> results = queryFactory
                .selectFrom(orderHeaders)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderHeaders.createdAt.desc())
                .fetch();

        long total = queryFactory
                .select(orderHeaders.count())
                .from(orderHeaders)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }

    public Page<OrderHeaders> findByRequestDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(orderHeaders.requestDate.between(startDate, endDate));

        List<OrderHeaders> results = queryFactory
                .selectFrom(orderHeaders)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderHeaders.createdAt.desc())
                .fetch();

        long total = queryFactory
                .select(orderHeaders.count())
                .from(orderHeaders)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
