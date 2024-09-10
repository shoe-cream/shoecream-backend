package com.springboot.order_header.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
import com.springboot.item.repository.ItemRepository;
import com.springboot.manufacture_history.repository.ManufactureHistoryRepository;
import com.springboot.manufacture_item.repository.ManufactureItemRepository;
import com.springboot.order_header.dto.SaleResponseDto;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.order_item.repository.OrderItemsRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SaleReport {
    private final OrderItemsRepository orderItemsRepository;
    private final ManufactureItemRepository manufactureItemRepository;
    private final ManufactureHistoryRepository manufactureHistoryRepository;
    private final ItemRepository itemRepository;

    public SaleReport(OrderItemsRepository orderItemsRepository, ManufactureItemRepository manufactureItemRepository, ManufactureHistoryRepository manufactureHistoryRepository, ItemRepository itemRepository) {
        this.orderItemsRepository = orderItemsRepository;
        this.manufactureItemRepository = manufactureItemRepository;
        this.manufactureHistoryRepository = manufactureHistoryRepository;
        this.itemRepository = itemRepository;
    }

    //기간별 레포트 (마진률, 판매량)
    public List<SaleResponseDto.SaleReportDto> getSaleReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<OrderItems> ordersInRange = orderItemsRepository.findByOrderHeadersRequestDateBetween(startDateTime, endDateTime);
        //List<ItemManufacture> manufacturesInRange = manufactureItemRepository.findByCreatedAtBetween(startDateTime, endDateTime);

        return new ArrayList<>(ordersInRange.stream()
                .collect(Collectors.toMap(OrderItems::getItemCD, orderItem -> {
                    SaleResponseDto.SaleReportDto reportDto = new SaleResponseDto.SaleReportDto();
                    reportDto.setItemCd(orderItem.getItemCD());
                    reportDto.setTotalOrdered(orderItemsRepository.findTotalOrderedByItemCD(orderItem.getItemCD(), startDateTime, endDateTime));
                    reportDto.setTotalManufactured(manufactureItemRepository.findTotalManufacturedByItemCdAndDateRange(orderItem.getItemCD(), startDateTime, endDateTime));
                    reportDto.setTotalOrderedPrice(getOrderTotalPrice(orderItem.getItemCD(), startDateTime, endDateTime));
                    reportDto.setTotalMfPrice(getManufactureTotalPrice(orderItem.getItemCD(), startDateTime, endDateTime));
                    reportDto.setMarginRate(calculateMarginRate(
                            getOrderTotalPrice(orderItem.getItemCD(), startDateTime, endDateTime),
                            getManufactureTotalPrice(orderItem.getItemCD(), startDateTime, endDateTime)
                    ));

                    return reportDto;
                }, (existing, replacement) -> existing)).values());
    }


    //현재 재고 확인
    public SaleResponseDto.InventoryDto getInventory(String itemCd) {
        Item item = findVerifiedItem(itemCd);
        SaleResponseDto.InventoryDto.InventoryDtoBuilder response = SaleResponseDto.InventoryDto.builder();
        response.itemId(item.getItemId());
        response.itemName(item.getItemNm());
        response.totalOrder(getOrderQuantity(itemCd));
        response.totalSupply(getManufacturedQuantity(itemCd));
        response.totalStock(calculateInventory(itemCd));
        return response.build();
    }


    //마진률 계산
    private BigDecimal calculateMarginRate(BigDecimal orderUnitPrice, BigDecimal manufactureUnitPrice) {
        BigDecimal total = BigDecimal.valueOf(1);
        if (orderUnitPrice != null && manufactureUnitPrice != null && orderUnitPrice.compareTo(BigDecimal.ZERO) > 0) {
            return total.subtract(manufactureUnitPrice.divide(orderUnitPrice, 2, RoundingMode.HALF_UP ));
        }
        return BigDecimal.ZERO;
    }

    //해당 기간의 총 판매가(승인 이후 상태만 확인해야함)
    private BigDecimal getOrderTotalPrice(String itemCd, LocalDateTime start, LocalDateTime end) {
        BigDecimal orderTotalPrice = orderItemsRepository.findTotalOrderPriceByItemCdAndOrderDateBetween(itemCd, start, end);
        return orderTotalPrice != null ? orderTotalPrice : BigDecimal.ZERO;
    }

    // 해당 기간의 총 공급가
    private BigDecimal getManufactureTotalPrice(String itemCd, LocalDateTime start, LocalDateTime end) {
        BigDecimal manufactureUnitPrice = manufactureItemRepository.findTotalSupplyPriceByItemCdAndManufactureDateBetween(itemCd, start, end);
        return manufactureUnitPrice != null ? manufactureUnitPrice : BigDecimal.ZERO;
    }

    //재고 계산
    private Integer calculateInventory(String itemCd) {
        Integer totalManufactured = getManufacturedQuantity(itemCd);
        Integer totalOrdered = getOrderQuantity(itemCd);
        Integer cal = totalManufactured - totalOrdered;
        return cal != null ? cal : 0;
    }

    //재고 계산을 위한 공급량
    private Integer getManufacturedQuantity(String itemCd) {
        Integer totalManufactured = manufactureItemRepository.findTotalManufacturedByItemCd(itemCd);
        return totalManufactured != null ? totalManufactured : 0;
    }

    //재고 계산을 위한 주문량 (승인 이후 상태의 주문량만 계산)
    private Integer getOrderQuantity(String itemCd) {
        Integer totalOrdered = orderItemsRepository.findTotalOrderedByItemCdAfterApproval(itemCd);
        return totalOrdered != null ? totalOrdered : 0;
    }

    //유효한 제품인지 검증
    private Item findVerifiedItem(String itemCd) {
        Optional<Item> item = itemRepository.findByItemCd(itemCd);
        return item.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }
}
