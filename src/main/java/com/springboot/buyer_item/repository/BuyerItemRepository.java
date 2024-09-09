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

//    @Query("SELECT b FROM BuyerItem b WHERE " +
//            "(:buyerCd IS NULL OR b.buyer.buyerCd = :buyerCd) AND " +
//            "(:buyerNm IS NULL OR b.buyer.buyerNm = :buyerNm) AND " +
//            "(:itemNm IS NULL OR b.item.itemNm = :itemNm) AND " +
//            "(:itemCd IS NULL OR b.item.itemCd = :itemCd)")
    Page<BuyerItem> findAllByBuyer_BuyerCdOrBuyer_BuyerNmOrItem_ItemCdOrItem_ItemNm(String buyerCd,
                                                                                    String buyerNm,
                                                                                    String itemNm,
                                                                                    String itemCd,
                                                                                    Pageable pageable);
}
