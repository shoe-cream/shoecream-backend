package com.springboot.buyer_item.repository;

import com.springboot.buyer_item.entity.BuyerItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BuyerItemRepository extends JpaRepository<BuyerItem, Long> {
    Page<BuyerItem> findAll(Pageable pageable);

    List<BuyerItem> findAllByItem_ItemCd(String itemCd);

}
