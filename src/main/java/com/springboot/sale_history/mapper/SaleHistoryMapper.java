package com.springboot.sale_history.mapper;


import com.springboot.member.entity.Member;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.sale_history.SaleHistoryDto;
import com.springboot.sale_history.entity.SaleHistory;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleHistoryMapper {
    default SaleHistory orderToSaleHistory(OrderHeaders orderHeader, Member member) {
        SaleHistory saleHistory = new SaleHistory();
        saleHistory.setEmployeeId(member.getEmployeeId());
        saleHistory.setCreatedAt(LocalDateTime.now());
        saleHistory.setOrderHeaders(orderHeader);

        return saleHistory;
    }
    default SaleHistoryDto saleHistoryToSaleHistoryResponseDto (SaleHistory saleHistory) {
        SaleHistoryDto.SaleHistoryDtoBuilder response = SaleHistoryDto.builder();
        response.createdAt(saleHistory.getCreatedAt());
        response.employeeId(saleHistory.getOrderHeaders().getMember().getEmployeeId());
        response.orderId(saleHistory.getOrderHeaders().getOrderId());
        response.orderStatus(saleHistory.getOrderHeaders().getOrderStatus());
        response.orderDate(saleHistory.getOrderHeaders().getCreatedAt());
        response.createdAt(saleHistory.getOrderHeaders().getCreatedAt());
        response.requestDate(saleHistory.getOrderHeaders().getRequestDate());
        response.buyerCd(saleHistory.getOrderHeaders().getBuyer().getBuyerCd());
        response.buyerNm(saleHistory.getOrderHeaders().getBuyer().getBuyerNm());
        response.orderItems(saleHistory.getOrderHeaders().getOrderItems());
        return response.build();
    }
    List<SaleHistoryDto> saleHistoriesToSaleHistoriesResponseDtos (List<SaleHistory> saleHistories);
}
