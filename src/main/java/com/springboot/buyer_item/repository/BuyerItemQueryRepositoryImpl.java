package com.springboot.buyer_item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.buyer.entity.QBuyer;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.buyer_item.entity.QBuyerItem;
import com.springboot.item.entity.QItem;
import com.springboot.manufacture.entity.QManufacture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BuyerItemQueryRepositoryImpl implements BuyerItemQueryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BuyerItemQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<BuyerItem> findBuyerItems(String buyerCd, String buyerNm, String itemCd, String itemNm, Pageable pageable) {

        QBuyerItem buyerItem = QBuyerItem.buyerItem;
        QBuyer buyer = QBuyer.buyer;
        QItem item = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();

        if (buyerCd != null && !buyerCd.isEmpty()) {
            builder.and(buyerItem.buyer.buyerCd.containsIgnoreCase(buyerCd));
        }

        if (buyerNm != null && !buyerNm.isEmpty()) {
            builder.and(buyerItem.buyer.buyerNm.containsIgnoreCase(buyerNm));
        }

        if (itemCd != null && !itemCd.isEmpty()) {
            builder.and(buyerItem.item.itemCd.containsIgnoreCase(itemCd));
        }

        if (itemNm != null && !itemNm.isEmpty()) {
            builder.and(buyerItem.item.itemNm.containsIgnoreCase(itemNm));
        }

        //sort 처리
        List<OrderSpecifier<?>> orderSpecifiers = getSortOrder(pageable, buyerItem);

        List<BuyerItem> results = queryFactory.selectFrom(buyerItem)
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(buyerItem)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<BuyerItem> findBuyerItemsByBuyerCdAndCurrentDate(String buyerCd, LocalDate currentDate, Pageable pageable) {
        QBuyerItem buyerItem = QBuyerItem.buyerItem;

        BooleanBuilder builder = new BooleanBuilder();
        if (buyerCd != null && !buyerCd.isEmpty()) {
            builder.and(buyerItem.buyer.buyerCd.eq(buyerCd));
        }

        if (currentDate != null) {
            LocalDateTime startOfDay = currentDate.atStartOfDay();
            LocalDateTime endOfDay = currentDate.atTime(23, 59, 59);

            builder.and(buyerItem.startDate.loe(endOfDay))
                    .and(buyerItem.endDate.goe(startOfDay));
        }

        List<OrderSpecifier<?>> orderSpecifiers = getSortOrder(pageable, buyerItem);

        List<BuyerItem> results = queryFactory.selectFrom(buyerItem)
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(buyerItem)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    private List<OrderSpecifier<?>> getSortOrder(Pageable pageable, QBuyerItem buyerItem) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(buyerItem.getType(), buyerItem.getMetadata());
            orders.add(new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(order.getProperty())));
        }
        return orders;
    }
}

