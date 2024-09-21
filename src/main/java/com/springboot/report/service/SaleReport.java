package com.springboot.report.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
import com.springboot.item.repository.ItemRepository;
import com.springboot.manufacture_item.repository.ManufactureItemRepository;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.order_item.repository.OrderItemQueryRepositoryCustom;
import com.springboot.order_item.repository.OrderItemsRepository;
import com.springboot.report.reportDto.ReportDto;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
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
    private final OrderItemQueryRepositoryCustom orderItemsRepository;
    private final ManufactureItemRepository manufactureItemRepository;
    private final ItemRepository itemRepository;

    public SaleReport(OrderItemQueryRepositoryCustom orderItemsRepository,
                      ManufactureItemRepository manufactureItemRepository,
                      ItemRepository itemRepository) {

        this.orderItemsRepository = orderItemsRepository;
        this.manufactureItemRepository = manufactureItemRepository;
        this.itemRepository = itemRepository;
    }

    //기간별 레포트 (마진률, 판매량)
    public List<ReportDto.SaleReportDto> getSaleReport(LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<OrderItems> ordersInRange = orderItemsRepository.findByOrderHeadersRequestDateBetween(null, startDateTime, endDateTime);

        return new ArrayList<>(ordersInRange.stream()
                .collect(Collectors.toMap(OrderItems::getItemCd, orderItem -> {
                    Optional<Item> item = itemRepository.findByItemCd(orderItem.getItemCd());
                    ReportDto.SaleReportDto reportDto = new ReportDto.SaleReportDto();
                    reportDto.setItemCd(orderItem.getItemCd());
                    reportDto.setItemNm(item.get().getItemNm());
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

    //마진률 계산
    private BigDecimal calculateMarginRate(BigDecimal orderUnitPrice, BigDecimal manufactureUnitPrice) {
        BigDecimal total = BigDecimal.valueOf(1);
        if (orderUnitPrice != null && manufactureUnitPrice != null && orderUnitPrice.compareTo(BigDecimal.ZERO) > 0) {
            return total.subtract(manufactureUnitPrice.divide(orderUnitPrice, 2, RoundingMode.HALF_UP )).multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    //해당 기간의 총 판매가(승인 이후 상태만 계산 = PRODUCT_PASS
    private BigDecimal getOrderTotalPrice(String itemCd, LocalDateTime start, LocalDateTime end) {
        BigDecimal orderTotalPrice = orderItemsRepository.findTotalOrderPriceByItemCdAndOrderDateBetween(itemCd, null, start, end);
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

}
