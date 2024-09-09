package com.springboot.buyer_item.service;

import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.mapper.BuyerMapper;
import com.springboot.buyer.repository.BuyerRepository;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.buyer_item.repository.BuyerItemRepository;
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

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BuyerItemService {
    private final BuyerItemRepository buyerItemRepository;
    private final BuyerRepository buyerRepository;
    private final ItemRepository itemRepository;

    public void createBuyerItem(BuyerItem buyerItem) {
        // 기본 아이템 정보 가져오기 (Item 테이블에서 가져옴)
        Item item = itemRepository.findById(buyerItem.getItem().getItemId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
        Buyer buyer = buyerRepository.findById(buyerItem.getBuyer().getBuyerId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));
        buyerItem.addBuyer(buyer);
        buyerItem.addItem(item);
        buyerItemRepository.save(buyerItem);
    }

    public BuyerItem findBuyerItem(long buyerItemId) {
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

    public Page<BuyerItem> findBuyerItems(int page, int size, String buyerCd, String buyerNm,
                                          String itemNm, String itemCd) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("buyerCd").descending());

        // 조건에 맞는 BuyerItems 조회
        return buyerItemRepository.findAllByBuyer_BuyerCdOrBuyer_BuyerNmOrItem_ItemCdOrItem_ItemNm(buyerCd, buyerNm, itemNm, itemCd, pageable);
    }

    // 수정 관련 한번더 확인
    public BuyerItem updateBuyerItem(BuyerItem buyerItem) {
        BuyerItem findedBuyerItem = buyerItemRepository.findById(buyerItem.getBuyerItemId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYERITEM_NOT_FOUND));

        Optional.ofNullable(buyerItem.getItem().getItemNm())
                .ifPresent(itemNm -> findedBuyerItem.getItem().setItemNm(itemNm));
        Optional.ofNullable(buyerItem.getItem().getUnit())
                .ifPresent(unit -> findedBuyerItem.getItem().setUnit(unit));
        Optional.ofNullable(buyerItem.getItem().getItemStatus())
                .ifPresent(itemStatus -> findedBuyerItem.getItem().setItemStatus(itemStatus));

        return buyerItemRepository.save(findedBuyerItem);
    }

    public void deleteBuyerItem(long buyerItemId) {
        Optional<BuyerItem> findBuyer = buyerItemRepository.findById(buyerItemId);
        BuyerItem buyer = findBuyer
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));
//        buyer.setItemStatus(BuyerItem.ItemStatus.DELETED)
        buyerItemRepository.save(buyer);
    }
}
