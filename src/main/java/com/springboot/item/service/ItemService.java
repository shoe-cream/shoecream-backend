package com.springboot.item.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
import com.springboot.item.repository.ItemRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    public void createItem(Item item, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public Item findItem(String itemCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        return findVerifiedItem(itemCd);
    }

    @Transactional(readOnly = true)
    public Page<Item> findItems(int page, int size, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = PageRequest.of(page, size, Sort.by("itemId").descending());

        Page<Item> items = itemRepository.findByItemStatusNot(Item.ItemStatus.NOT_FOR_SALE, pageable);
        return items;
    }

    public Item updateItem(Item item, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

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

    public void deleteItem(long itemId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));

        item.setItemStatus(Item.ItemStatus.NOT_FOR_SALE);

        itemRepository.save(item);
    }

    public void deleteItems(List<Long> itemIds) {
        // 각 ID에 대해 아이템 조회 후 상태 변경 (삭제 처리)
        List<Item> items = itemRepository.findByItemIdIn(itemIds);

        for (Item item : items) {
            item.setItemStatus(Item.ItemStatus.NOT_FOR_SALE);  // 상태를 판매중지로 변경
        }
        itemRepository.saveAll(items);
    }

    private Item findVerifiedItem(String itemCd) {
        Optional<Item> item = itemRepository.findByItemCd(itemCd);

        return item.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String username = (String) authentication.getPrincipal();

        return memberRepository.findByEmployeeId(username)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
