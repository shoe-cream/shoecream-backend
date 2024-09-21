package com.springboot.item.repository;

import com.springboot.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemQueryRepositoryCustom {

    Page<Item> findItemsByCondition(String itemNm, String itemCd, Pageable pageable);
}
