package com.springboot.manufacture_item.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
import com.springboot.item.repository.ItemRepository;
import com.springboot.manufacture_item.entity.ItemManufacture;
import com.springboot.manufacture_item.repository.ManufactureItemRepository;
import com.springboot.manufacture.entity.Manufacture;
import com.springboot.manufacture.repository.ManufactureRepository;
import com.springboot.manufacture_history.entity.ManuFactureHistory;
import com.springboot.manufacture_history.mapper.ManufactureHistoryMapper;
import com.springboot.manufacture_history.repository.ManufactureHistoryRepository;
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
@Transactional
@RequiredArgsConstructor
public class ManufactureItemService {
    private final ManufactureItemRepository itemMfRepository;
    private final ItemRepository itemRepository;
    private final ManufactureRepository manufactureRepository;
    private final ManufactureHistoryRepository manuFactureHistoryRepository;
    private final ManufactureHistoryMapper manufactureHistoryMapper;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public ItemManufacture createItemMf(ItemManufacture itemManufacture, Authentication authentication) {
        extractMemberFromAuthentication(authentication);
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

    public ItemManufacture findItemMf(long mfItemId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Optional<ItemManufacture> itemManufacture = itemMfRepository.findById(mfItemId);

        ItemManufacture itemMf = itemManufacture
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));

        return itemMf;
    }
    
    public Page<ItemManufacture> findItemMfs(int page, int size, String itemCd, String mfCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = PageRequest.of(page, size, Sort.by("manufacture.mfCd").descending());
        
        if((itemCd == null || itemCd.isEmpty()) && (mfCd == null || mfCd.isEmpty())) {
            return itemMfRepository.findAll(pageable);
        }
        if(itemCd != null && !itemCd.isEmpty()) {
            return itemMfRepository.findByItem_ItemCd(itemCd, pageable);
        }
            return itemMfRepository.findByManufacture_MfCd(mfCd, pageable);
    }

    public ItemManufacture updateItemMf(ItemManufacture itemManufacture, Authentication authentication){
        extractMemberFromAuthentication(authentication);

        ItemManufacture findItemMf = itemMfRepository.findById(itemManufacture.getMfItemId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));

        Optional.ofNullable(itemManufacture.getUnitPrice())
                .ifPresent(unitPrice -> findItemMf.setUnitPrice(unitPrice));
        Optional.ofNullable(itemManufacture.getQty())
                .ifPresent(qty -> findItemMf.setQty(qty));

        findItemMf.setModifiedAt(LocalDateTime.now());

        return itemMfRepository.save(findItemMf);
    }

    private ItemManufacture verifyItemMf(long mfItemId) {
        ItemManufacture itemManufacture = itemMfRepository.findById(mfItemId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));

        return itemManufacture;
    }

    private Member verifiedMember(Authentication authentication) {
        String user = (String) authentication.getPrincipal();
        return memberService.findVerifiedEmployee(user);
    }

    public Page<ManuFactureHistory> findHistories (int page, int size, Long mfItemId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return manuFactureHistoryRepository.findByMfItemId(mfItemId, pageable);
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String username = (String) authentication.getPrincipal();

        return memberRepository.findByEmployeeId(username)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
