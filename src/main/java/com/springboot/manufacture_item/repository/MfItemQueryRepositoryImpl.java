package com.springboot.manufacture_item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.item.entity.QItem;
import com.springboot.manufacture.entity.QManufacture;
import com.springboot.manufacture_item.entity.ItemManufacture;
import com.springboot.manufacture_item.entity.QItemManufacture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MfItemQueryRepositoryImpl implements MfItemQueryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MfItemQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public Page<ItemManufacture> findItemManufacture(String itemNm, String itemCd, String mfNm, String mfCd, String region, Pageable pageable) {

        QItemManufacture itemManufacture = QItemManufacture.itemManufacture;
        QItem item = QItem.item;
        QManufacture manufacture = QManufacture.manufacture;

        BooleanBuilder builder = new BooleanBuilder();

        if (itemNm != null && !itemNm.isEmpty()) {
            builder.and(itemManufacture.item.itemNm.containsIgnoreCase(itemNm));
        }

        if (itemCd != null && !itemCd.isEmpty()) {
            builder.and(itemManufacture.item.itemCd.containsIgnoreCase(itemCd));
        }

        if (mfNm != null && !mfNm.isEmpty()) {
            builder.and(itemManufacture.manufacture.mfNm.containsIgnoreCase(mfNm));
        }

        if (mfCd != null && !mfCd.isEmpty()) {
            builder.and(itemManufacture.manufacture.mfCd.containsIgnoreCase(mfCd));
        }

        //sort 처리
        List<OrderSpecifier<?>> orderSpecifiers = getSortOrder(pageable, itemManufacture);

        List<ItemManufacture> results = queryFactory.selectFrom(itemManufacture)
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(itemManufacture)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    private List<OrderSpecifier<?>> getSortOrder(Pageable pageable, QItemManufacture itemManufacture) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(itemManufacture.getType(), itemManufacture.getMetadata());
            orders.add(new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(order.getProperty())));
        }
        return orders;
    }
}
