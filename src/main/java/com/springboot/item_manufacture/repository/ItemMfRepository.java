package com.springboot.item_manufacture.repository;

import com.springboot.item_manufacture.entity.ItemManufacture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemMfRepository extends JpaRepository<ItemManufacture, Long> {
    Page<ItemManufacture> findByItem_ItemCd(String itemCd, Pageable pageable);
    Page<ItemManufacture> findByManufacture_MfCd(String mfCd, Pageable pageable);
}
