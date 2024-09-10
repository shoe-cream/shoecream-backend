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

    public void createBuyerItem(BuyerItem buyerItem, Authentication authentication) {
        // 기본 아이템 정보 가져오기 (Item 테이블에서 가져옴)
        extractMemberFromAuthentication(authentication);
        Item item = itemRepository.findById(buyerItem.getItem().getItemId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
        Buyer buyer = buyerRepository.findById(buyerItem.getBuyer().getBuyerId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));
        buyerItem.addBuyer(buyer);
        buyerItem.addItem(item);
        buyerItemRepository.save(buyerItem);
    }

    public BuyerItem findBuyerItem(long buyerItemId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Optional<BuyerItem> buyerIt = buyerItemRepository.findById(buyerItemId);

        BuyerItem buyerItem = buyerIt
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYERITEM_NOT_FOUND));

        return buyerItem;
    }

    // 페이지네이션으로 필터링을통해 전체조회 ok 필터링 없이 전체를 조회 ok.
//    public Page<BuyerItem> findBuyerItems(int page, int size,
//                                          String buyerCd,
//                                          String buyerNm,
//                                          String ItemCd,
//                                          String ItemNm) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("buyerCd").descending());
//
//        if (buyerCd != null && !buyerCd.isEmpty()) {
//            return buyerItemRepository.findAllByBuyerCdOrBuyerNmOrItemCdOrItemNm(buyerCd, buyerNm, ItemCd, ItemNm, pageable);
//        } else {
//            return buyerItemRepository.findAll(pageable);
//        }
//    }

    public Page<BuyerItem> findBuyerItems(int page, int size, String buyerCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = PageRequest.of(page, size, Sort.by("buyer.buyerCd").descending());

        if(buyerCd == null || buyerCd.isEmpty()) {
            // buyerCd가 없으면 전체 데이터를 조회
            return buyerItemRepository.findAll(pageable);
        } else {
            // buyerCd가 있으면 해당 바이어 코드에 맞는 데이터 조회
            return buyerItemRepository.findAllByBuyer_BuyerCd(buyerCd, pageable);
        }
    }

    // 수정 관련 한번더 확인
    public BuyerItem updateBuyerItem(BuyerItem buyerItem, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        BuyerItem findedBuyerItem = buyerItemRepository.findById(buyerItem.getBuyerItemId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYERITEM_NOT_FOUND));

        Optional.ofNullable(buyerItem.getItem().getItemNm())
                .ifPresent(itemNm -> findedBuyerItem.getItem().setItemNm(itemNm));
        Optional.ofNullable(buyerItem.getItem().getUnit())
                .ifPresent(unit -> findedBuyerItem.getItem().setUnit(unit));
        Optional.ofNullable(buyerItem.getItem().getItemStatus())
                .ifPresent(itemStatus -> findedBuyerItem.getItem().setItemStatus(itemStatus));

        findedBuyerItem.setModifiedAt(LocalDateTime.now());

        return buyerItemRepository.save(findedBuyerItem);
    }

    public void deleteBuyerItem(long buyerItemId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Optional<BuyerItem> findBuyer = buyerItemRepository.findById(buyerItemId);
        BuyerItem buyer = findBuyer
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));
//        buyer.setItemStatus(BuyerItem.ItemStatus.DELETED)
        buyerItemRepository.save(buyer);
    }

    private Member verifiedMember(Authentication authentication) {

        String user = (String) authentication.getPrincipal();
        return memberService.findVerifiedEmployee(user);
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String username = (String) authentication.getPrincipal();

        return memberRepository.findByEmployeeId(username)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
