package com.springboot.item.repository;

import com.springboot.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByItemCd(String itemCd);
    List<Item> findAll();
    boolean existsByItemCd(String itemCd);
    boolean existsByItemNm(String itemNm);
    Optional<Item> findByItemId(Long itemId);
    Optional<Item> findByItemNm(String itemNm);
    Page<Item> findAllByItemStatusNot(Item.ItemStatus itemStatus, Pageable pageable);
    List<Item> findAllByItemStatusNot(Item.ItemStatus itemStatus);

    @Query("SELECT i FROM Item i WHERE REPLACE(LOWER(i.itemNm), ' ', '') = LOWER(:itemNm) AND i.itemStatus <> :itemStatus")
    Page<Item> findByItemNmIgnoreCaseWithoutSpacesAndItemStatusNot(@Param("itemNm") String itemNm,
                                                                   @Param("itemStatus") Item.ItemStatus itemStatus,
                                                                   Pageable pageable);

    @Query("SELECT i FROM Item i WHERE REPLACE(LOWER(i.itemCd), ' ', '') = LOWER(:itemCd) AND i.itemStatus <> :itemStatus")
    Page<Item> findByItemCdIgnoreCaseAndItemStatusNot(String itemCd,
                                                      Item.ItemStatus itemStatus,
                                                      Pageable pageable);
}
