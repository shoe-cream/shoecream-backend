package com.springboot.manufacture.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.buyer.entity.Buyer;
import com.springboot.manufacture.entity.Manufacture;
import com.springboot.manufacture.entity.QManufacture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MFQueryRepositoryImpl implements MFQueryRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public MFQueryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<Manufacture> findManufactures(String mfCd, String mfNm, String email, String region, Pageable pageable) {

        QManufacture manufacture = QManufacture.manufacture;

        BooleanBuilder builder = new BooleanBuilder();

        // 비활성화(삭제)는 전체 조회 pagination 에서 보이지 않게 설정
        builder.and(manufacture.manufactureStatus.eq(Manufacture.ManufactureStatus.ACTIVE));

        if (mfCd != null && !mfCd.isEmpty()) {
            builder.and(manufacture.mfCd.containsIgnoreCase(mfCd));
        }

        if (mfNm != null && !mfNm.isEmpty()) {
            builder.and(manufacture.mfNm.containsIgnoreCase(mfNm));
        }

        if (email != null && !email.isEmpty()) {
            builder.and(manufacture.email.containsIgnoreCase(email));
        }

        if (region != null && !region.isEmpty()) {
            builder.and(manufacture.region.containsIgnoreCase(region));
        }

        List<Manufacture> results = jpaQueryFactory.selectFrom(manufacture)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.selectFrom(manufacture)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);

    }
}
