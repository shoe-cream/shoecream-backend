package com.springboot.buyer_item.repository;

import com.springboot.buyer_item.entity.BuyerItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface BuyerItemQueryRepositoryCustom {

    Page<BuyerItem> findBuyerItems(String buyerCd, String buyerNm, String itemCd, String itemNm, Pageable pageable);

    Page<BuyerItem> findBuyerItemsByBuyerCdAndCurrentDate(String buyerCd, LocalDateTime currentDate, Pageable pageable);
}
