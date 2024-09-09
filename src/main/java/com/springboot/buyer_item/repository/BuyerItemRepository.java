package com.springboot.buyer_item.repository;


import com.springboot.buyer_item.entity.BuyerItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BuyerItemRepository extends JpaRepository<BuyerItem, Long> {
    Page<BuyerItem> findAll(Pageable pageable);
    List<BuyerItem> findAllByBuyerCd(String buyerCd);

    @Query("SELECT b FROM BuyerItem b WHERE " +
            "(:buyerCd IS NULL OR b.buyerCd = :buyerCd) AND " +
            "(:buyerNm IS NULL OR b.buyerNm = :buyerNm) AND " +
            "(:itemNm IS NULL OR b.itemNm = :itemNm) AND " +
            "(:itemCd IS NULL OR b.itemCd = :itemCd)")
    Page<BuyerItem> findAllByFilters(@Param("buyerCd") String buyerCd,
                                     @Param("buyerNm") String buyerNm,
                                     @Param("itemNm") String itemNm,
                                     @Param("itemCd") String itemCd,
                                     Pageable pageable);
}
