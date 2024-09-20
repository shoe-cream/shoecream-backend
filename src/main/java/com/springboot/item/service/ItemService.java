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
import java.util.UUID;

import static com.springboot.utils.PageableCreator.createPageable;

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
            verifiedExists(item.getItemNm());
            String itemCd = createItemCd();
            item.setItemCd(itemCd);
            itemRepository.save(item);
        });
    }

    //item Cd로 item 찾기
    @Transactional(readOnly = true)
    public Item findItem(String itemCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);
        return findVerifiedItem(itemCd);
    }

    //item Nm로 item 찾기
    @Transactional(readOnly = true)
    public Item findItemByNm(String itemNm) {
        Item item = itemRepository.findByItemNm(itemNm)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));

        isDeleted(item);

        return item;
    }

    //전체 item 조회 - pagination
    @Transactional(readOnly = true)
    public Page<Item> findItems(int page, int size, String itemNm, String itemCd, String criteria, String direction, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = createPageable(page, size, criteria, direction);
        Page<Item> items;

        if ((itemNm == null || itemNm.isEmpty())  && (itemCd == null || itemCd.isEmpty())) {

            items = itemRepository.findAllByItemStatusNot(Item.ItemStatus.INACTIVE, pageable);

        } else if (itemCd == null || itemCd.isEmpty()) {

            String compareItemNm = itemNm.trim().toLowerCase().replaceAll("\\s", "");
            items =  itemRepository.findByItemNmIgnoreCaseWithoutSpacesAndItemStatusNot(compareItemNm, Item.ItemStatus.INACTIVE, pageable);

        } else {
            String compareItemCd = itemCd.trim().toLowerCase().replaceAll("\\s", "");
            items = itemRepository.findByItemCdIgnoreCaseAndItemStatusNot(compareItemCd, Item.ItemStatus.INACTIVE, pageable);

        }

        return items;
    }

    //전체 item 조회
    public List<Item> findItemsAll() {
        return itemRepository.findAllByItemStatusNot(Item.ItemStatus.INACTIVE);
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
        item.setItemStatus(Item.ItemStatus.INACTIVE);

        itemRepository.save(item);
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
        Item item = itemRepository.findByItemCd(itemCd)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));

      //  isDeleted(item);

        return item;
    }

    //검증된 item 찾기 - itemId를 통해
    public Item findVerifiedItemId(Long itemId) {
        Item item = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));

      //  isDeleted(item);

        return item;
    }

    //검증된 item 찾기 - itemNm를 통해
    public Item findVerifiedItemNm(String itemNm) {
        Item item = itemRepository.findByItemNm(itemNm)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));

        isDeleted(item);

        return item;
    }

    public void isDeleted (Item item) {
        if(item.getItemStatus().equals(Item.ItemStatus.INACTIVE)) {
            throw new BusinessLogicException(ExceptionCode.INACTIVE_STATUS);
        }
    }

    // 제품 코드 생성 메서드
    private String createItemCd() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 3).toUpperCase();

        return "AD" + uuid;
    }
}
