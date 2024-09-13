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

    //item 생성
    public void createItem(List<Item> items, Authentication authentication) {
        extractMemberFromAuthentication(authentication);
        items.stream().forEach(item -> {
            verifiedExistsItemCd(item.getItemCd());
            verifiedExists(item.getItemNm());
            itemRepository.save(item);
        });
    }

    //item Cd로 item 찾기
    @Transactional(readOnly = true)
    public Item findItem(String itemCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);
        return findVerifiedItem(itemCd);
    }

    //전체 item 조회 - pagination
    @Transactional(readOnly = true)
    public Page<Item> findItems(int page, int size, String criteria, String direction, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = createPageable(page, size, criteria, direction);
        Page<Item> items = itemRepository.findAll(pageable);

        return items;
    }

    //전체 item 조회
    public List<Item> findItemsAll() {
        return itemRepository.findAll();
    }

    //item 정보 수정 (이름/단위/단가/상태)
    public Item updateItem(Item patch, Authentication authentication) {
        //member 검증
        extractMemberFromAuthentication(authentication);

        //변경 전 Item
        Item findItem = findVerifiedItemId(patch.getItemId());

        //변경할 필드값
        Optional.ofNullable(patch.getItemNm())
                .ifPresent(itemNm -> {
                    if(!itemNm.equals(findItem.getItemNm())) {
                        verifiedExists(itemNm);
                        findItem.setItemNm(itemNm);
                    }
                });
        Optional.ofNullable(patch.getUnit())
                .ifPresent(findItem::setUnit);
        Optional.ofNullable(patch.getUnitPrice())
                .ifPresent(findItem::setUnitPrice);
        Optional.ofNullable(patch.getItemStatus())
                .ifPresent(findItem::setItemStatus);

        // 변경 저장
        findItem.setModifiedAt(LocalDateTime.now());

        return itemRepository.save(findItem);
    }

    //item DB에서 삭제
    public void deleteItem(long itemId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Item item = findVerifiedItemId(itemId);

        itemRepository.delete(item);
    }

    //authentication -> member 정보 가져오기
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

    //검증된 item 찾기 - itemCd를 통해
    private Item findVerifiedItem(String itemCd) {
        Optional<Item> item = itemRepository.findByItemCd(itemCd);

        return item.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }

    //검증된 item 찾기 - itemId를 통해
    public Item findVerifiedItemId(Long itemId) {
        Optional<Item> item = itemRepository.findByItemId(itemId);

        return item.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }

    //Pageable 생성
    private Pageable createPageable(int page, int size, String sortCriteria, String direction) {

        Sort.Direction sortDirection = (direction == null || direction.isEmpty()) ? Sort.Direction.DESC : Sort.Direction.fromString(direction);

        Sort sort = Sort.by(sortDirection, sortCriteria);

        return PageRequest.of(page, size, sort);
    }
}
