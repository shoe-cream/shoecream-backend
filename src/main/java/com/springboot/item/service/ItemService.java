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

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    public void createItem(List<Item> items, Authentication authentication) {
        extractMemberFromAuthentication(authentication);
        items.stream().forEach(item -> {
            verifiedExistsItemCd(item.getItemCd());
            verifiedExists(item.getItemNm());
            itemRepository.save(item);
        });
    }

    @Transactional(readOnly = true)
    public Item findItem(String itemCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);
        return findVerifiedItem(itemCd);
    }

    @Transactional(readOnly = true)
    public Page<Item> findItems(int page, int size, String criteria, String direction, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = createPageable(page, size, criteria, direction);

        Page<Item> items = itemRepository.findByItemStatusNot(Item.ItemStatus.NOT_FOR_SALE, pageable);
        return items;
    }

    private Pageable createPageable(int page, int size, String sortCriteria, String direction) {

        Sort.Direction sortDirection = (direction == null || direction.isEmpty()) ? Sort.Direction.DESC : Sort.Direction.fromString(direction);

        Sort sort = Sort.by(sortDirection, sortCriteria);

        return PageRequest.of(page, size, sort);
    }

    public Item updateItem(Item patch, Authentication authentication) {
        extractMemberFromAuthentication(authentication);
        Item findItem = findVerifiedItemId(patch.getItemId());
        Optional.ofNullable(patch.getItemNm())
                .ifPresent(itemNm -> {
                    if(!itemNm.equals(findItem.getItemNm())) {
                        verifiedExists(itemNm);
                        findItem.setItemNm(itemNm);
                    }
                });
        Optional.ofNullable(patch.getUnit())
                .ifPresent(unit -> findItem.setUnit(unit));
        Optional.ofNullable(patch.getUnitPrice())
                .ifPresent(unitPrice -> findItem.setUnitPrice(unitPrice));
        Optional.ofNullable(patch.getItemStatus())
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

    private Item findVerifiedItem(String itemCd) {
        Optional<Item> item = itemRepository.findByItemCd(itemCd);

        return item.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String username = (String) authentication.getPrincipal();

        return memberRepository.findByEmployeeId(username)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    // item code 중복 검사
    private void verifiedExistsItemCd(String itemCd) {
        if(itemRepository.existsByItemCd(itemCd)) {
            throw new BusinessLogicException(ExceptionCode.ITEM_CD_ALREADY_EXISTS);
        }
    }

    // item name 중복 검사
    private void verifiedExists(String itemNm) {
        if(itemRepository.existsByItemNm(itemNm)) {
            throw new BusinessLogicException(ExceptionCode.ITEM_NAME_ALREADY_EXISTS);
        }
    }

    public Item findVerifiedItemId(Long itemId) {
        Optional<Item> item = itemRepository.findByItemId(itemId);

        return item.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }

    public List<Item> findVerifiedItems(List<Long> itemIds) {
        return itemRepository.findByItemIdIn(itemIds);
    }
}
