package com.springboot.buyer_item.service;

import com.springboot.buyer.service.BuyerService;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.buyer_item.repository.BuyerItemRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.service.ItemService;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import static com.springboot.utils.PageableCreator.createPageable;
import org.springframework.data.domain.Pageable;
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
    private final BuyerService buyerService;
    private final ItemService itemService;
    private final MemberService memberService;

    //Buyer-Item 생성
    public void createBuyerItem(List<BuyerItem> buyerItems, Authentication authentication) {
        // 기본 아이템 정보 가져오기 (Item 테이블에서 가져옴)
        extractMemberFromAuthentication(authentication);

        buyerItems.stream().forEach(buyerItem -> {
            buyerItem.addItem(itemService.findItemByNm(buyerItem.getItem().getItemNm()));
            buyerItem.addBuyer(buyerService.findVerifiedBuyerByBuyerNm(buyerItem.getBuyer().getBuyerNm()));


            buyerItemRepository.save(buyerItem);
        });
    }

    //ItemCd로 BuyerItem 조회
    public BuyerItem findBuyerItem(String buyerItemCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        return findVerifiedBuyerItemByItemCd(buyerItemCd);
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

    //BuyerItem 수정 (단가/단가시작일/단가종료일)
    public BuyerItem updateBuyerItem(BuyerItem buyerItem, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        BuyerItem findedBuyerItem = findVerifiedBuyerItem(buyerItem.getBuyerItemId());

        //여기서 item이 아닌 BuyerItem만 수정할 수 없어야 함. 만약 수정해야한다면 필드로 가지고 있어야한다.

        Optional.ofNullable(buyerItem.getUnitPrice())
                .ifPresent(findedBuyerItem::setUnitPrice);
        Optional.ofNullable(buyerItem.getStartDate())
                .ifPresent(findedBuyerItem::setStartDate);
        Optional.ofNullable(buyerItem.getEndDate())
                .ifPresent(findedBuyerItem::setEndDate);

        findedBuyerItem.setModifiedAt(LocalDateTime.now());

        return buyerItemRepository.save(findedBuyerItem);
    }

    // DB 에서 삭제 - 가급적 자제
    public void deleteBuyerItem(long buyerItemId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        BuyerItem buyerItem = findVerifiedBuyerItem(buyerItemId);

        buyerItemRepository.delete(buyerItem);
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String username = (String) authentication.getPrincipal();

        return memberService.findVerifiedEmployee(username);
    }

    //buyerItemId로 검증
    private BuyerItem findVerifiedBuyerItem(Long buyerItemId) {
        return buyerItemRepository.findById(buyerItemId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.BUYER_ITEM_NOT_FOUND));
    }

    //buyerItemCd로 검증
    private BuyerItem findVerifiedBuyerItemByItemCd(String buyerItemCd) {
        return buyerItemRepository.findByItem_ItemCd(buyerItemCd)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.BUYER_ITEM_NOT_FOUND));
    }
}
