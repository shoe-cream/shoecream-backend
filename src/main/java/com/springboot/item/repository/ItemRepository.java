package com.springboot.item.repository;

import com.springboot.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAll();

    boolean existsByItemCd(String itemCd);
    boolean existsByItemNm(String itemNm);

    Optional<Item> findByItemId(Long itemId);
    Optional<Item> findByItemNm(String itemNm);
    Optional<Item> findByItemCd(String itemCd);

    List<Item> findAllByItemStatusNot(Item.ItemStatus itemStatus);
}
