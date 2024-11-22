package com.springboot.manufacture_item.repository;

import com.springboot.manufacture_item.entity.ItemManufacture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface MfItemQueryRepositoryCustom {

    Page<ItemManufacture> findItemManufacture(String itemNm, String itemCd,
                                              String mfNm, String mfCd,
                                              String region, Pageable pageable);

    List<ItemManufacture> findManufacturedItemsForOrderItems (String employeeId, LocalDateTime start, LocalDateTime end);
}
