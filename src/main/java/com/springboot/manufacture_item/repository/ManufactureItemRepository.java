package com.springboot.manufacture_item.repository;

import com.springboot.manufacture_item.entity.ItemManufacture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufactureItemRepository extends JpaRepository<ItemManufacture, Long> {
    Page<ItemManufacture> findByItem_ItemCd(String itemCd, Pageable pageable);
    Page<ItemManufacture> findByManufacture_MfCd(String mfCd, Pageable pageable);
}
