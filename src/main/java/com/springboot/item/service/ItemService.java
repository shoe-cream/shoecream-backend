package com.springboot.item.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
import com.springboot.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public void createItem(Item item) {
        itemRepository.save(item);
    }

    public Item findItem(String itemCd) {
        return findVerifiedItem(itemCd);
    }

    public Page<Item> findItems(String itemNm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if(itemNm == null || itemNm.isEmpty()) {
            return itemRepository.findAll(pageable);
        }else {
            return itemRepository.findByItemNm(itemNm, pageable);
        }

    }

    public Item updateItem(Item item) {
        Item findItem = findVerifiedItem(item.getItemCd());

        Optional.ofNullable(item.getItemNm())
                .ifPresent(itemNm -> findItem.setItemNm(itemNm));
        Optional.ofNullable(item.getUnit())
                .ifPresent(unit -> findItem.setUnit(unit));
        Optional.ofNullable(item.getUnitPrice())
                .ifPresent(unitPrice -> findItem.setUnitPrice(unitPrice));
        Optional.ofNullable(item.getItemStatus())
                .ifPresent(itemStatus -> findItem.setItemStatus(itemStatus));

        findItem.setModifiedAt(LocalDateTime.now());

        return itemRepository.save(findItem);
    }

    private Item findVerifiedItem(String itemCd) {
        Optional<Item> item = itemRepository.findByItemCd(itemCd);

        return item.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }


}
