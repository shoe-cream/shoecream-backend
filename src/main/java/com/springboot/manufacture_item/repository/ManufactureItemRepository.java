package com.springboot.manufacture_item.repository;

import com.springboot.manufacture_item.entity.ItemManufacture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ManufactureItemRepository extends JpaRepository<ItemManufacture, Long> {

    //기간별 전체 공급량
    @Query("SELECT SUM(im.qty) FROM ItemManufacture im WHERE im.item.itemCd = :itemCd AND im.createdAt BETWEEN :startDateTime AND :endDateTime")
    Integer findTotalManufacturedByItemCdAndDateRange(
            @Param("itemCd") String itemCd,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    // 전체 공급량 계산
    @Query("SELECT SUM(im.qty) FROM ItemManufacture im WHERE im.item.itemCd = :itemCd")
    Integer findTotalManufacturedByItemCd(@Param("itemCd") String itemCd);

    @Query("SELECT SUM(im.unitPrice * im.qty) FROM ItemManufacture im " +
            "JOIN im.item i WHERE i.itemCd = :itemCd AND im.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal findTotalSupplyPriceByItemCdAndManufactureDateBetween(
            @Param("itemCd") String itemCd,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
