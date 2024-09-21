package com.springboot.buyer.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.entity.QBuyer;
import com.springboot.buyer_item.entity.QBuyerItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BuyerQueryRepositoryImpl implements BuyerQueryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public BuyerQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Buyer> findBuyer(String buyerNm, String buyerCd, String tel, String address, String businessType,
                                 Pageable pageable) {

        QBuyer buyer = QBuyer.buyer;

        BooleanBuilder builder = new BooleanBuilder();

        // 비활성화(삭제)는 전체 조회 pagination 에서 보이지 않게 설정
        builder.and(buyer.buyerStatus.eq(Buyer.BuyerStatus.ACTIVE));

        if(buyerCd != null && !buyerCd.isEmpty()) {
            builder.and(buyer.buyerCd.containsIgnoreCase(buyerCd));
        }

        if(buyerNm != null && !buyerNm.isEmpty()) {
            builder.and(buyer.buyerNm.containsIgnoreCase(buyerNm));
        }

        if(tel != null && !tel.isEmpty()) {
            builder.and(buyer.tel.containsIgnoreCase(tel));
        }

        if(address != null && !address.isEmpty()) {
            builder.and(buyer.address.containsIgnoreCase(address));
        }

        if(businessType != null && !businessType.isEmpty()) {
            builder.and(buyer.businessType.containsIgnoreCase(businessType));
        }

        List<OrderSpecifier<?>> orderSpecifiers = getSortOrder(pageable, buyer);

        List<Buyer> results = jpaQueryFactory.selectFrom(buyer)
                .where(builder)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.selectFrom(buyer)
                .where(builder)
                .fetchCount();

        return  new PageImpl<>(results, pageable, total);
    }

    private List<OrderSpecifier<?>> getSortOrder(Pageable pageable, QBuyer buyer) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(buyer.getType(), buyer.getMetadata());
            orders.add(new OrderSpecifier(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(order.getProperty())));
        }
        return orders;
    }
}
