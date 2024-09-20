package com.springboot.buyer_item.service;

import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.service.BuyerService;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.buyer_item.repository.BuyerItemRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class BuyerItemService {
    private final BuyerItemRepository buyerItemRepository;
    private final BuyerService buyerService;
    private final ItemService itemService;
    private final MemberService memberService;

//    //Buyer-Item 생성
//    public void createBuyerItem(List<BuyerItem> buyerItems, Authentication authentication) {
//        // 기본 아이템 정보 가져오기 (Item 테이블에서 가져옴)
//        extractMemberFromAuthentication(authentication);
//
//        buyerItems.stream().forEach(buyerItem -> {
//            //단가적용기간 중복 검사
//            Item item = itemService.findVerifiedItemNm(buyerItem.getItem().getItemNm());
//            checkPriceApplicationDateOverlap(buyerItem.getBuyer().getBuyerNm(), item.getItemCd(), buyerItem.getStartDate());
//
//            buyerItem.addItem(item);
//            buyerItem.addBuyer(buyerService.findVerifiedBuyerByBuyerNm(buyerItem.getBuyer().getBuyerNm()));
//
//            buyerItemRepository.save(buyerItem);
//        });
//    }

    //Buyer-Item 생성
    public void createBuyerItem(List<BuyerItem> buyerItems, Authentication authentication) {
        // 기본 아이템 정보 가져오기 (Item 테이블에서 가져옴)
        extractMemberFromAuthentication(authentication);

        buyerItems.stream().forEach(buyerItem -> {
            //단가적용기간 중복 검사
            Item item = itemService.findVerifiedItemNm(buyerItem.getItem().getItemNm());
            List<BuyerItem> buyerItemList = recentGetBuyerItem(buyerItem.getBuyer().getBuyerNm(), item.getItemCd(), buyerItem.getStartDate());
            if(!buyerItemList.isEmpty()) {
                throw new BusinessLogicException(ExceptionCode.PERIOD_OVERLAP_ERROR);
            }

            buyerItem.addItem(item);
            buyerItem.addBuyer(buyerService.findVerifiedBuyerByBuyerNm(buyerItem.getBuyer().getBuyerNm()));

            buyerItemRepository.save(buyerItem);
        });
    }

    //ItemCd로 BuyerItem 조회
    public List<BuyerItem> findBuyerItem(String buyerItemCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        return findVerifiedBuyerItemByItemCd(buyerItemCd);
    }

    public Page<BuyerItem> findBuyerItems(int page, int size, String buyerCd, String criteria, String direction, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = createPageable(page, size, criteria, direction);
        LocalDateTime today = LocalDateTime.now();

        if(buyerCd == null || buyerCd.isEmpty()) {
            // buyerCd가 없으면 전체 데이터를 조회
            return buyerItemRepository.findAll(pageable);
        } else {
            // buyerCd가 있으면 해당 바이어 코드에 맞는 데이터 조회
            return buyerItemRepository.findBuyerItemsByBuyerCdAndCurrentDate(buyerCd, today, pageable);

        }
    }

    //BuyerItem 수정 (단가/단가시작일/단가종료일)
    public BuyerItem updateBuyerItem(BuyerItem buyerItem, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        BuyerItem findedBuyerItem = findVerifiedBuyerItem(buyerItem.getBuyerItemId());

        //여기서 item이 아닌 BuyerItem만 수정할 수 없어야 함. 만약 수정해야한다면 필드로 가지고 있어야한다.

        //구매 단가 수정
        Optional.ofNullable(buyerItem.getUnitPrice())
                .ifPresent(findedBuyerItem::setUnitPrice);

        // 단가 적용 시작일 수정
        Optional.ofNullable(buyerItem.getStartDate())
                .ifPresent(startDate -> {
                    //변경하려는 시작일이 있는 buyerItemList -> 하나라도 있으면 수정할 수 없다.
                    List<BuyerItem> buyerItems = recentGetBuyerItem(findedBuyerItem.getBuyer().getBuyerNm(),findedBuyerItem.getItem().getItemCd(), startDate);
                    if(!buyerItems.isEmpty()) {
                        throw new BusinessLogicException(ExceptionCode.PERIOD_OVERLAP_ERROR);
                    }

                    //겹치지 않으면 변경일 저장
                    findedBuyerItem.setStartDate(startDate);
                });

        //단가 적용 종료일 수정
        Optional.ofNullable(buyerItem.getEndDate())
                .ifPresent(endDate -> {
                    //종료일이 시작일보다 앞설 수 없다
                    if (findedBuyerItem.getStartDate().isAfter(endDate)){
                        throw new BusinessLogicException(ExceptionCode.PERIOD_OVERLAP_ERROR);
                    }

                    //시작일 이후라면 종료일 저장
                    findedBuyerItem.setEndDate(endDate);
                });

        findedBuyerItem.setModifiedAt(LocalDateTime.now());

        return buyerItemRepository.save(findedBuyerItem);
    }

    // DB 에서 삭제 - 가급적 자제
    public void deleteBuyerItem(long buyerItemId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        BuyerItem buyerItem = findVerifiedBuyerItem(buyerItemId);

        buyerItemRepository.delete(buyerItem);
    }

    //member 정보 가져오기
    private Member extractMemberFromAuthentication(Authentication authentication) {
        String username = (String) authentication.getPrincipal();

        return memberService.findVerifiedEmployee(username);
    }

    //buyerItemId로 검증
    private BuyerItem findVerifiedBuyerItem(Long buyerItemId) {
        return buyerItemRepository.findById(buyerItemId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.BUYER_ITEM_NOT_FOUND));
    }

    //ItemCd로 검증
    private List<BuyerItem> findVerifiedBuyerItemByItemCd(String itemCd) {
        return buyerItemRepository.findAllByItem_ItemCd(itemCd);
    }

    //buyer-itemCD가 동일한 여러 개가 있을 수 있음. 만약 없다면 기간중복은 보지 않고 등록 및 수정 가능
    //단가적용기간 중복 검사
    private void checkPriceApplicationDateOverlap(String buyerNm, String itemCd, LocalDateTime startDate) {
        Buyer buyer = buyerService.findVerifiedBuyerByBuyerNm(buyerNm);
        buyer.getBuyerItems()
                .stream()
                .forEach(buyerItem -> {
                    List<BuyerItem> buyerItems = findVerifiedBuyerItemByItemCd(itemCd);

                    for(BuyerItem findBuyerItem : buyerItems) {
                        //2023-09-01 ~2023-12-01 일때 새로 적용되는 startDate가 endDate 이전일때
                        if(startDate.isBefore(findBuyerItem.getEndDate())) {
                            throw new BusinessLogicException(ExceptionCode.PERIOD_OVERLAP_ERROR);
                        }
                    }
                });
    }

    private List<BuyerItem> recentGetBuyerItem(String buyerNm, String itemCd, LocalDateTime date) {
        Buyer buyer = buyerService.findVerifiedBuyerByBuyerNm(buyerNm);

        return buyer.getBuyerItems()
                .stream()
                .filter(findBuyerItem -> !findBuyerItem.getStartDate().isAfter(date) && !findBuyerItem.getEndDate().isBefore(date)) // 날짜 조건 필터
                .filter(findBuyerItem -> findBuyerItem.getItem().getItemCd().equals(itemCd)) // itemCd 조건 필터
                .collect(Collectors.toList());
    }
}
