package com.springboot.order_header.dto;

import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_item.entity.OrderItems;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Getter
    public static class Post {
        @NotNull(message = "buyerCd가 누락되었습니다.")
        private String buyerCd;
        @NotNull(message = "orderItem 이 누락되었습니다.")
        private List<OrderItemDto> orderItems;
        @NotNull(message = "납기일이 누락되었습니다.")
        private LocalDateTime requestDate;
    }

    @Getter
    public static class OrderPatch {
        @Setter
        private Long orderId;
        private LocalDateTime requestDate;
        private OrderHeaders.OrderStatus orderStatus;
    }

    @Getter
    public static class ItemPatch {
        private Long orderId;
        private Long itemId;
        private BigDecimal unitPrice;
        private Long qty;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    @Builder
    @Getter
    public static class Response {
        private Long orderId;
        private String employeeId;
        private String buyerCd;
        private String buyerNm;
        private String orderCd;
        private LocalDateTime createdAt;
        private LocalDateTime requestDate;
        private OrderHeaders.OrderStatus status;
        private List<OrderItems> orderItems;
    }


    @Getter
    public static class OrderItemDto {
        @NotNull(message = "itemCd가 누락되었습니다.")
        private String itemCd;

        @NotNull(message = "unitPrice 가 누락되었습니다.")
        private BigDecimal unitPrice;

        @NotNull(message = "qty 이 누락되었습니다.")
        private Long qty;

        @NotNull(message = "startDate 가 누락되었습니다.")
        private LocalDateTime startDate;

        @NotNull(message = "endDate 가 누락되었습니다.")
        private LocalDateTime endDate;

        @NotNull(message = "unit 이 누락되었습니다.")
        private String unit;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @AllArgsConstructor
    public static class OrderSearchRequest {
        private String buyerCd;
        private String itemCd;
        private OrderHeaders.OrderStatus status;
        private String OrderCd;

        @DateTimeFormat(pattern = "yyyyMMdd")
        private LocalDate searchStartDate = LocalDate.of(1900, 1, 1);  // 기본값

        @DateTimeFormat(pattern = "yyyyMMdd")
        private LocalDate searchEndDate = LocalDate.of(9999, 12, 31); // 기본값

    }

    @Getter
    public static class ApprovalOrRejectDto {
        @NotNull
        private String orderCd;
        private String rejectReason;
    }
}

