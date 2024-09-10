package com.springboot.sale_history.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.sale_history.SaleHistoryDto;
import com.springboot.sale_history.entity.SaleHistory;
import com.springboot.sale_history.entity.SaleHistoryItems;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleHistoryMapper {
    default SaleHistory orderToSaleHistory(OrderHeaders orderHeader, Member member) {
        SaleHistory saleHistory = new SaleHistory();
        saleHistory.setEmployeeId(member.getEmployeeId());
        saleHistory.setCreatedAt(LocalDateTime.now());
        saleHistory.setPersonInCharge(orderHeader.getMember().getEmployeeId());
        saleHistory.setOrderStatus(orderHeader.getOrderStatus());
        saleHistory.setRequestDate(orderHeader.getRequestDate());
        saleHistory.setOrderId(orderHeader.getOrderId());
        saleHistory.setOrderDate(orderHeader.getCreatedAt());
        saleHistory.setBuyerCd(orderHeader.getBuyer().getBuyerCd());

        // Capture the state of OrderItems and map to SaleHistoryItems
        for (OrderItems orderItem : orderHeader.getOrderItems()) {
            SaleHistoryItems saleHistoryItem = new SaleHistoryItems(orderItem);
            saleHistoryItem.setSaleHistory(saleHistory);
            saleHistory.getSaleHistoryItems().add(saleHistoryItem);
        }

        return saleHistory;
    }

    SaleHistoryDto saleHistoryToSaleHistoryResponseDto (SaleHistory saleHistory);
//    default SaleHistoryDto saleHistoryToSaleHistoryResponseDto (SaleHistory saleHistory) {
//        SaleHistoryDto.SaleHistoryDtoBuilder response = SaleHistoryDto.builder();
//        response.createdAt(saleHistory.getCreatedAt());
//        response.employeeId(saleHistory.getOrderHeaders().getMember().getEmployeeId());
//        response.orderId(saleHistory.getOrderHeaders().getOrderId());
//        response.orderStatus(saleHistory.getOrderHeaders().getOrderStatus());
//        response.orderDate(saleHistory.getOrderHeaders().getCreatedAt());
//        response.createdAt(saleHistory.getOrderHeaders().getCreatedAt());
//        response.requestDate(saleHistory.getOrderHeaders().getRequestDate());
//        response.buyerCd(saleHistory.getOrderHeaders().getBuyer().getBuyerCd());
//        response.buyerNm(saleHistory.getOrderHeaders().getBuyer().getBuyerNm());
//        response.orderItems(saleHistory.getOrderHeaders().getOrderItems());
//        return response.build();
//    }
    List<SaleHistoryDto> saleHistoriesToSaleHistoriesResponseDtos (List<SaleHistory> saleHistories);
}
