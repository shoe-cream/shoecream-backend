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
    Page<BuyerItem> findAllByBuyer_BuyerCd(String buyerCd, Pageable pageable);

//    @Query("SELECT bi FROM BuyerItem bi JOIN FETCH bi.buyer JOIN FETCH bi.item WHERE bi.buyer.buyerCd = :buyerCd")
//    List<BuyerItem> findAllByBuyerCd(@Param("buyerCd") String buyerCd);
}
