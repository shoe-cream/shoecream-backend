package com.springboot.manufacture_item.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.item.entity.QItem;
import com.springboot.manufacture.entity.QManufacture;
import com.springboot.manufacture_item.entity.ItemManufacture;
import com.springboot.manufacture_item.entity.QItemManufacture;
import com.springboot.member.entity.QMember;
import com.springboot.order_header.entity.QOrderHeaders;
import com.springboot.order_item.entity.QOrderItems;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Override
    public List<ItemManufacture> findManufacturedItemsForOrderItems(String employeeId, LocalDateTime start, LocalDateTime end) {

        QItemManufacture itemManufacture = QItemManufacture.itemManufacture;
        QOrderItems orderItems = QOrderItems.orderItems;
        QOrderHeaders orderHeaders = QOrderHeaders.orderHeaders;
        QMember member = QMember.member;
        QItem item = QItem.item;

        // 판매된 제품의 itemCd 목록을 가져오기
        List<String> orderItemCds = queryFactory.select(orderItems.itemCd)
                .from(orderItems)
                .join(orderItems.orderHeaders, orderHeaders) // orderHeaders와 조인
                .join(orderHeaders.member, member) // member와 조인
                .where(
                        member.employeeId.eq(employeeId)
                                .and(orderHeaders.requestDate.between(start, end))
                )
                .fetch();

        // 제조된 제품의 itemCd와 조인하여 일치하는 항목 찾기
        return queryFactory.selectFrom(itemManufacture)
                .join(itemManufacture.item, item)  // ItemManufacture와 Item을 조인
                .where(
                        item.itemCd.in(orderItemCds)  // Item의 itemCd와 OrderItems의 itemCd 비교
                                .and(itemManufacture.createdAt.before(
                                        JPAExpressions.select(orderHeaders.requestDate.max()) // requestDate의 최대값을 선택
                                                .from(orderItems)
                                                .join(orderItems.orderHeaders, orderHeaders)
                                                .where(orderItems.itemCd.eq(item.itemCd))
                                ))
                )
                .fetch();
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
