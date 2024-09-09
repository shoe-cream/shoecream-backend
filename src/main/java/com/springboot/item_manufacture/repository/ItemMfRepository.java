package com.springboot.item_manufacture.repository;

import com.springboot.item_manufacture.entity.ItemManufacture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemMfRepository extends JpaRepository<ItemManufacture, Long> {
}
