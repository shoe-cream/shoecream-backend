package com.springboot.order_header.dto;

import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_item.entity.OrderItems;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Getter
    public static class Post {
        private String buyerCD;
        private List<OrderItemDto> orderItems;
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
        private BigDecimal unitPrice;
        private Long quantity;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    @Builder
    @Getter
    public static class Response {
        private Long orderId;
        private String buyerCD;
        private String buyerNm;
        private LocalDateTime createdAt;
        private LocalDateTime requestDate;
        private OrderHeaders.OrderStatus status;
        private List<OrderItems> orderItems;
    }


    @Getter
    public static class OrderItemDto {
        private String itemCD;
        private BigDecimal unitPrice;
        private Long quantity;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String unit;
    }
}

