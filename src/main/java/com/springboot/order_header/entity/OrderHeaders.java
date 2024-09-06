package com.springboot.order_header.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springboot.buyer.Buyer;
import com.springboot.member.entity.Member;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.sale_history.entity.SaleHistory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderHeaders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    @Column(nullable = false)
    private String buyerCd;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private OrderStatus orderStatus = OrderStatus.REQUEST_TEMP;

    @OneToMany(mappedBy = "orderHeaders")
    @JsonManagedReference
    private List<OrderItems> orderItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @JsonBackReference
    private Member member;

    @ManyToOne
    @JoinColumn(name = "buyer_id", updatable = false)
    @JsonBackReference
    private Buyer buyer;

    @OneToMany(mappedBy = "orderHeaders")
    @JsonManagedReference
    private List<SaleHistory> saleHistory = new ArrayList<>();

    public enum OrderStatus {
        REQUEST_TEMP("견적요청"),
        PURCHASE_REQUEST("발주요청"),
        APPROVED("승인완료"),
        SHIPPED("출하완료"),
        PRODUCT_PASS("제품합격"),
        PRODUCT_FAIL("불합격"),
        CANCELLED("취소");

        OrderStatus(String description) {
            this.description = description;
        }

        @Getter
        @Setter
        private String description;
    }
}