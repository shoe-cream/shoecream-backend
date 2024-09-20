package com.springboot.buyer_item.repository;

import com.springboot.buyer_item.entity.BuyerItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuyerItemQueryRepositoryCustom {

    Page<BuyerItem> findBuyerItems(String buyerCd, String buyerNm, String itemCd, String itemNm, Pageable pageable);
}
