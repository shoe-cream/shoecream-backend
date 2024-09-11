package com.springboot.order_header.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
import com.springboot.item.repository.ItemRepository;
import com.springboot.manufacture_history.repository.ManufactureHistoryRepository;
import com.springboot.manufacture_item.repository.ManufactureItemRepository;
import com.springboot.order_header.dto.OrderReportDto;
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
    public List<OrderReportDto.SaleReportDto> getSaleReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<OrderItems> ordersInRange = orderItemsRepository.findByOrderHeadersRequestDateBetween(startDateTime, endDateTime);
        //List<ItemManufacture> manufacturesInRange = manufactureItemRepository.findByCreatedAtBetween(startDateTime, endDateTime);

        return new ArrayList<>(ordersInRange.stream()
                .collect(Collectors.toMap(OrderItems::getItemCd, orderItem -> {
                    OrderReportDto.SaleReportDto reportDto = new OrderReportDto.SaleReportDto();
                    reportDto.setItemCd(orderItem.getItemCd());
                    reportDto.setTotalOrdered(getTotalOrderedPeriod(orderItem.getItemCd(), startDateTime, endDateTime));
                    reportDto.setTotalManufactured(getTotalManufacturedPeriod(orderItem.getItemCd(), startDateTime, endDateTime));
                    reportDto.setTotalOrderedPrice(getOrderTotalPrice(orderItem.getItemCd(), startDateTime, endDateTime));
                    reportDto.setTotalMfPrice(getManufactureTotalPrice(orderItem.getItemCd(), startDateTime, endDateTime));
                    reportDto.setMarginRate(calculateMarginRate(
                            getOrderTotalPrice(orderItem.getItemCd(), startDateTime, endDateTime),
                            getManufactureTotalPrice(orderItem.getItemCd(), startDateTime, endDateTime)
                    ));

                    return reportDto;
                }, (existing, replacement) -> existing)).values());
    }


    //현재 재고 확인
    public OrderReportDto.InventoryDto getInventory(String itemCd) {
        Item item = findVerifiedItem(itemCd);
        OrderReportDto.InventoryDto.InventoryDtoBuilder response = OrderReportDto.InventoryDto.builder();
        response.itemId(item.getItemId());
        response.itemName(item.getItemNm());
        response.totalOrder(getOrderQty(itemCd));
        response.totalSupply(getManufacturedQty(itemCd));
        response.unusedStock(getUnusedItemQty(itemCd));
        response.preparedOrder(getPreparedItemQty(itemCd));
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

    //해당 기간의 총 판매가(승인 이후 상태만 계산 = APPROVED, SHIPPED, PRODUCT_PASS
    private BigDecimal getOrderTotalPrice(String itemCd, LocalDateTime start, LocalDateTime end) {
        BigDecimal orderTotalPrice = orderItemsRepository.findTotalOrderPriceByItemCdAndOrderDateBetween(itemCd, start, end);
        return orderTotalPrice != null ? orderTotalPrice : BigDecimal.ZERO;
    }

    // 해당 기간의 총 공급가
    private BigDecimal getManufactureTotalPrice(String itemCd, LocalDateTime start, LocalDateTime end) {
        BigDecimal manufactureUnitPrice = manufactureItemRepository.findTotalSupplyPriceByItemCdAndManufactureDateBetween(itemCd, start, end);
        return manufactureUnitPrice != null ? manufactureUnitPrice : BigDecimal.ZERO;
    }

    //해당 기간의 총 판매량
    private Integer getTotalOrderedPeriod (String itemCd, LocalDateTime startDate, LocalDateTime endDate) {
        Integer total = orderItemsRepository.findTotalOrderedByItemCd(itemCd, startDate, endDate);
        return total != null ? total : 0 ;
    }

    //해당 기간의 총 공급량
    private Integer getTotalManufacturedPeriod (String itemCd, LocalDateTime startDate, LocalDateTime endDate) {
        Integer total = manufactureItemRepository.findTotalManufacturedByItemCdAndDateRange(itemCd, startDate, endDate);
        return total != null ? total : 0 ;
    }

    //재고 계산 (총 공급량 - 총 주문량(승인 이후) - 불용재고량
    public Integer calculateInventory(String itemCd) {
        Integer totalManufactured = getManufacturedQty(itemCd);
        Integer totalOrdered = getOrderQty(itemCd);
        Integer totalUnused = getUnusedItemQty(itemCd);
        Integer stock =  totalManufactured - totalOrdered - totalUnused;
        if (stock < 0) {
            throw new BusinessLogicException(ExceptionCode.OUT_OF_STOCK);
        }
        return stock;
    }

    //재고 계산을 위한 공급량
    private Integer getManufacturedQty(String itemCd) {
        Integer totalManufactured = manufactureItemRepository.findTotalManufacturedByItemCd(itemCd);
        return totalManufactured != null ? totalManufactured : 0;
    }

    //재고 계산을 위한 주문량 (승인 이후 상태의 주문량만 계산 = APPROVED, SHIPPED, PRODUCT_PASS)
    private Integer getOrderQty(String itemCd) {
        Integer totalOrdered = orderItemsRepository.findTotalOrderedByItemCdAfterApproval(itemCd);
        return totalOrdered != null ? totalOrdered : 0;
    }

    //주문 대기량 (상태 = 견적요청, 발주요청)
    private Integer getPreparedItemQty(String itemCd) {
        Integer totalUnused = orderItemsRepository.findTotalPreparationOrderByItemCd(itemCd);
        return totalUnused != null ? totalUnused : 0;
    }

    // 불용재고량 (상태 = PRODUCT_FAIL)
    private Integer getUnusedItemQty(String itemCd) {
        Integer totalUnused = orderItemsRepository.findTotalUnusedByItemCd(itemCd);
        return totalUnused != null ? totalUnused : 0;
    }

    //유효한 제품인지 검증
    private Item findVerifiedItem(String itemCd) {
        Optional<Item> item = itemRepository.findByItemCd(itemCd);
        return item.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }
}
