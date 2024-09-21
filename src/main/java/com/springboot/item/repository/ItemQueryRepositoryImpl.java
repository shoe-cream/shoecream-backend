package com.springboot.item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.item.entity.Item;
import com.springboot.item.entity.QItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ItemQueryRepositoryImpl implements ItemQueryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ItemQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Item> findItemsByCondition(String itemNm, String itemCd, Pageable pageable) {
        QItem item = QItem.item;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(item.itemStatus.ne(Item.ItemStatus.INACTIVE));

        if (itemNm != null && !itemNm.trim().isEmpty()) {
            builder.and(item.itemNm.containsIgnoreCase(itemNm));
        }

        if (itemCd != null && !itemCd.trim().isEmpty()) {
            builder.and(item.itemCd.containsIgnoreCase(itemCd));
        }

        List<OrderSpecifier<?>> orderSpecifiers = getSortOrder(pageable, item);


        List<Item> results = queryFactory
                .selectFrom(item)
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(item)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    private List<OrderSpecifier<?>> getSortOrder(Pageable pageable, QItem item) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(item.getType(), item.getMetadata());
            orders.add(new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(order.getProperty())));
        }
        return orders;
    }
}
