package com.springboot.buyer_item.service;

import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.repository.BuyerRepository;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.buyer_item.repository.BuyerItemRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
import com.springboot.item.repository.ItemRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BuyerItemService {
    private final BuyerItemRepository buyerItemRepository;
    private final BuyerRepository buyerRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public void createBuyerItem(List<BuyerItem> buyerItems, Authentication authentication) {
        // 기본 아이템 정보 가져오기 (Item 테이블에서 가져옴)
        extractMemberFromAuthentication(authentication);

        buyerItems.stream().forEach(buyerItem -> {
            buyerItem.addItem(findVerifiedItem(buyerItem.getItem().getItemNm()));
            buyerItem.addBuyer(findVerifiedBuyer(buyerItem.getBuyer().getBuyerNm()));
            buyerItemRepository.save(buyerItem);
        });
    }

    public BuyerItem findBuyerItem(long buyerItemId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        return findVerifiedBuyerItem(buyerItemId);
    }

    public Page<BuyerItem> findBuyerItems(int page, int size, String buyerCd, String criteria, String direction, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = createPageable(page, size, criteria, direction);

        if(buyerCd == null || buyerCd.isEmpty()) {
            // buyerCd가 없으면 전체 데이터를 조회
            return buyerItemRepository.findAll(pageable);
        } else {
            // buyerCd가 있으면 해당 바이어 코드에 맞는 데이터 조회
            return buyerItemRepository.findAllByBuyer_BuyerCd(buyerCd, pageable);
        }
    }

    private Pageable createPageable(int page, int size, String sortCriteria, String direction) {

        Sort.Direction sortDirection = (direction == null || direction.isEmpty()) ? Sort.Direction.DESC : Sort.Direction.fromString(direction);

        Sort sort = Sort.by(sortDirection, sortCriteria);

        return PageRequest.of(page, size, sort);
    }

    public BuyerItem updateBuyerItem(BuyerItem buyerItem, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        BuyerItem findedBuyerItem = buyerItemRepository.findById(buyerItem.getBuyerItemId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_ITEM_NOT_FOUND));

        //여기서 item 정보는 수정할 수 없어야 함. 만약 수정해야한다면 필드로 가지고 있어야한다.

        Optional.ofNullable(buyerItem.getUnitPrice())
                .ifPresent(findedBuyerItem::setUnitPrice);
        Optional.ofNullable(buyerItem.getStartDate())
                .ifPresent(findedBuyerItem::setStartDate);
        Optional.ofNullable(buyerItem.getEndDate())
                .ifPresent(findedBuyerItem::setEndDate);

        findedBuyerItem.setModifiedAt(LocalDateTime.now());

        return buyerItemRepository.save(findedBuyerItem);
    }

    // DB 에서 삭제
    public void deleteBuyerItem(long buyerItemId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        BuyerItem buyerItem = findVerifiedBuyerItem(buyerItemId);

        buyerItemRepository.delete(buyerItem);
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String username = (String) authentication.getPrincipal();

        return memberRepository.findByEmployeeId(username)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    private Item findVerifiedItem(String itemNm) {
        return itemRepository.findByItemNm(itemNm)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }

    private Buyer findVerifiedBuyer (String buyerNm) {
        return  buyerRepository.findByBuyerNm(buyerNm)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));
    }

    private BuyerItem findVerifiedBuyerItem(Long buyerItemId) {
        return buyerItemRepository.findById(buyerItemId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.BUYER_ITEM_NOT_FOUND));
    }
}
