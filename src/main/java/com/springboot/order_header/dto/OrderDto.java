package com.springboot.order_header.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Getter
    public static class Post {
        private String buyerCD;
        private List<OrderItemDto> orderItemDtoList;
        private LocalDateTime requestDate;
    }

    @Getter
    public static class Patch {

    }

    @Getter
    public static class Response {

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

