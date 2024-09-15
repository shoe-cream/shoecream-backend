package com.springboot.manufacture_item.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
import com.springboot.item.service.ItemService;
import com.springboot.manufacture.service.ManufactureService;
import com.springboot.manufacture_item.entity.ItemManufacture;
import com.springboot.manufacture_item.repository.ManufactureItemRepository;
import com.springboot.manufacture.entity.Manufacture;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ManufactureItemService {
    private final ManufactureItemRepository itemMfRepository;
    private final ItemService itemService;
    private final ManufactureService manufactureService;
    private final ManufactureHistoryRepository manufactureHistoryRepository;
    private final ManufactureHistoryMapper manufactureHistoryMapper;
    private final MemberService memberService;

    public void createItemMf(List<ItemManufacture> itemManufactures, Authentication authentication) {
        Member member = extractMemberFromAuthentication(authentication);

        itemManufactures.stream().forEach(itemManufacture -> {
            Item item = itemService.findVerifiedItemNm(itemManufacture.getItem().getItemNm());
            Manufacture manufacture = manufactureService.verifyManufactureByNm(itemManufacture.getManufacture().getMfNm());

            itemManufacture.addItem(item);
            itemManufacture.addMf(manufacture);

            ItemManufacture saveItemManufacture = itemMfRepository.save(itemManufacture);
            manufactureHistoryRepository.save(manufactureHistoryMapper.manufactureHistoryToItemManufacture(saveItemManufacture, member));
        });
    }

    public ItemManufacture findItemMf(long mfItemId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        return verifyItemMf(mfItemId);
    }
    
    public Page<ItemManufacture> findItemMfs(int page, int size, String criteria,String direction, String itemCd, String mfCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = createPageable(page, size, criteria, direction);
        
        if((itemCd == null || itemCd.isEmpty()) && (mfCd == null || mfCd.isEmpty())) {
            return itemMfRepository.findAll(pageable);
        }
        if(itemCd != null && !itemCd.isEmpty()) {
            return itemMfRepository.findByItem_ItemCd(itemCd, pageable);
        }
            return itemMfRepository.findByManufacture_MfCd(mfCd, pageable);
    }

    public Page<ManuFactureHistory> findHistories (int page, int size, String criteria, String direction, Long mfItemId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = createPageable(page, size, criteria, direction);

        return manufactureHistoryRepository.findByMfItemId(mfItemId, pageable);
    }

    private Pageable createPageable(int page, int size, String sortCriteria, String direction) {

        Sort.Direction sortDirection = (direction == null || direction.isEmpty()) ? Sort.Direction.DESC : Sort.Direction.fromString(direction);

        Sort sort = Sort.by(sortDirection, sortCriteria);

        return PageRequest.of(page, size, sort);
    }


    public ItemManufacture updateItemMf(ItemManufacture itemManufacture, Authentication authentication){
        Member member = extractMemberFromAuthentication(authentication);

        ItemManufacture findItemMf = verifyItemMf(itemManufacture.getMfItemId());

        Optional.ofNullable(itemManufacture.getUnitPrice())
                .ifPresent(findItemMf::setUnitPrice);
        Optional.ofNullable(itemManufacture.getQty())
                .ifPresent(findItemMf::setQty);

        findItemMf.setModifiedAt(LocalDateTime.now());

        //DB에 저장
        ItemManufacture savedItemManufacture = itemMfRepository.save(findItemMf);
        manufactureHistoryRepository.save(manufactureHistoryMapper.manufactureHistoryToItemManufacture(savedItemManufacture, member));

        return savedItemManufacture;
    }

    private ItemManufacture verifyItemMf(long mfItemId) {
        ItemManufacture itemManufacture = itemMfRepository.findById(mfItemId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));

        return itemManufacture;
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String username = (String) authentication.getPrincipal();

        return memberService.findVerifiedEmployee(username);
    }
}
