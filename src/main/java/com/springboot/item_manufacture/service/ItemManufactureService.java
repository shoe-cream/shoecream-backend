package com.springboot.item_manufacture.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
import com.springboot.item.repository.ItemRepository;
import com.springboot.item_manufacture.entity.ItemManufacture;
import com.springboot.item_manufacture.mapper.ItemMfMapper;
import com.springboot.item_manufacture.repository.ItemMfRepository;
import com.springboot.manufacture.entity.Manufacture;
import com.springboot.manufacture.repository.ManufactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public void createItemMf(ItemManufacture itemManufacture) {
        Item item = itemRepository.findById(itemManufacture.getItem().getItemId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
        Manufacture manufacture = manufactureRepository.findById(itemManufacture.getManufacture().getMfId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));
        itemManufacture.addItem(item);
        itemManufacture.addMf(manufacture);
        itemMfRepository.save(itemManufacture);
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
}
