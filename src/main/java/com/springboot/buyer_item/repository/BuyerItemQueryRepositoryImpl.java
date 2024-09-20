package com.springboot.buyer_item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.buyer.entity.QBuyer;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.buyer_item.entity.QBuyerItem;
import com.springboot.item.entity.QItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

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
            builder.and(buyerItem.buyer.buyerCd.eq(buyerCd));
        }

        if (buyerNm != null && !buyerNm.isEmpty()) {
            builder.and(buyerItem.buyer.buyerNm.containsIgnoreCase(buyerNm));
        }

        if (itemCd != null && !itemCd.isEmpty()) {
            builder.and(buyerItem.item.itemCd.eq(itemCd));
        }

        if (itemNm != null && !itemNm.isEmpty()) {
            builder.and(buyerItem.item.itemNm.containsIgnoreCase(itemNm));
        }

        List<BuyerItem> results = queryFactory.selectFrom(buyerItem)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(buyerItem)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}

