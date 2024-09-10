package com.springboot.item_manufacture.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
import com.springboot.item.repository.ItemRepository;
import com.springboot.item_manufacture.entity.ItemManufacture;
import com.springboot.item_manufacture.repository.ItemMfRepository;
import com.springboot.manufacture.entity.Manufacture;
import com.springboot.manufacture.repository.ManufactureRepository;
import com.springboot.manufacture_history.entity.ManuFactureHistory;
import com.springboot.manufacture_history.mapper.ManufactureHistoryMapper;
import com.springboot.manufacture_history.repository.ManufactureHistoryRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemManufactureService {
    private final ItemMfRepository itemMfRepository;
    private final ItemRepository itemRepository;
    private final ManufactureRepository manufactureRepository;
    private final ManufactureHistoryRepository manuFactureHistoryRepository;
    private final ManufactureHistoryMapper manufactureHistoryMapper;
    private final MemberService memberService;

    public ItemManufacture createItemMf(ItemManufacture itemManufacture, Authentication authentication) {
        Member member = verifiedMember(authentication);
        Item item = itemRepository.findById(itemManufacture.getItem().getItemId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
        Manufacture manufacture = manufactureRepository.findById(itemManufacture.getManufacture().getMfId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));
        itemManufacture.addItem(item);
        itemManufacture.addMf(manufacture);

        ItemManufacture saveItemManufacture = itemMfRepository.save(itemManufacture);
        manuFactureHistoryRepository.save(manufactureHistoryMapper.manufactureHistoryToItemManufacture(saveItemManufacture, member));
        return saveItemManufacture;
    }

    public ItemManufacture findItemMf(long mfItemId) {
        Optional<ItemManufacture> itemManufacture = itemMfRepository.findById(mfItemId);

        ItemManufacture itemMf = itemManufacture
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));

        return itemMf;
    }
    
    public Page<ItemManufacture> findItemMfs(int page, int size, String itemCd, String mfCd) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("manufacture.mfCd").descending());
        
        if((itemCd == null || itemCd.isEmpty()) && (mfCd == null || mfCd.isEmpty())) {
            return itemMfRepository.findAll(pageable);
        }
        if(itemCd != null && !itemCd.isEmpty()) {
            return itemMfRepository.findByItem_ItemCd(itemCd, pageable);
        }
            return itemMfRepository.findByManufacture_MfCd(mfCd, pageable);
    }

    private Member verifiedMember(Authentication authentication) {

        String user = (String) authentication.getPrincipal();
        return memberService.findVerifiedEmployee(user);
    }


    public Page<ManuFactureHistory> findHistories (int page, int size, Long mfItemId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return manuFactureHistoryRepository.findByMfItemId(mfItemId, pageable);
    }
}
