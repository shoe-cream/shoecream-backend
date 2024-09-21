package com.springboot.report.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
import com.springboot.item.repository.ItemRepository;
import com.springboot.manufacture_item.repository.ManufactureItemRepository;
import com.springboot.order_item.repository.OrderItemQueryRepositoryCustom;
import com.springboot.report.reportDto.ReportDto;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InventoryReport {

    private final ManufactureItemRepository manufactureItemRepository;
    private final OrderItemQueryRepositoryCustom orderItemsRepository;
    private final ItemRepository itemRepository;

    public InventoryReport(ManufactureItemRepository manufactureItemRepository,
                            OrderItemQueryRepositoryCustom orderItemsRepository,
                            ItemRepository itemRepository) {
        this.manufactureItemRepository = manufactureItemRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.itemRepository = itemRepository;
    }

    // 현재 재고 확인
    public ReportDto.InventoryDto getInventory(String itemCd) {
        Item item = findVerifiedItem(itemCd);
        ReportDto.InventoryDto.InventoryDtoBuilder response = ReportDto.InventoryDto.builder();
        response.itemId(item.getItemId());
        response.itemName(item.getItemNm());
        response.totalOrder(getOrderQty(itemCd));
        response.totalSupply(getManufacturedQty(itemCd));
        response.unusedStock(getUnusedItemQty(itemCd));
        response.preparedOrder(getPreparedItemQty(itemCd));
        response.totalStock(calculateInventory(itemCd));
        return response.build();
    }

    // 재고 계산 (총 공급량 - 총 주문량(승인이후) - 불용재고량)
    public Integer calculateInventory(String itemCd) {
        Integer totalManufactured = getManufacturedQty(itemCd);
        Integer totalOrdered = getOrderQty(itemCd);
        Integer totalUnused = getUnusedItemQty(itemCd);
        Integer stock = totalManufactured - totalOrdered - totalUnused;
        if (stock < 0) {
            throw new BusinessLogicException(ExceptionCode.OUT_OF_STOCK);
        }
        return stock;
    }

    // 재고 계산을 위한 공급량
    private Integer getManufacturedQty(String itemCd) {
        Integer totalManufactured = manufactureItemRepository.findTotalManufacturedByItemCd(itemCd);
        return totalManufactured != null ? totalManufactured : 0;
    }

    // 재고 계산을 위한 주문량
    private Integer getOrderQty(String itemCd) {
        Integer totalOrdered = orderItemsRepository.findTotalOrderedByItemCdAfterApproval(itemCd);
        return totalOrdered != null ? totalOrdered : 0;
    }

    // 주문 대기량
    private Integer getPreparedItemQty(String itemCd) {
        Integer totalUnused = orderItemsRepository.findTotalPreparationOrderByItemCd(itemCd);
        return totalUnused != null ? totalUnused : 0;
    }

    // 불용재고량
    private Integer getUnusedItemQty(String itemCd) {
        Integer totalUnused = orderItemsRepository.findTotalUnusedByItemCd(itemCd);
        return totalUnused != null ? totalUnused : 0;
    }

    // 유효한 제품인지 검증
    private Item findVerifiedItem(String itemCd) {
        Optional<Item> item = itemRepository.findByItemCd(itemCd);
        return item.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }
}