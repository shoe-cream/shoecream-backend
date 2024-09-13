package com.springboot.sale_history.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springboot.order_header.entity.OrderHeaders;

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
public class SaleHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long saleHistoryId;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String employeeId;

    @Column
    private String orderCd;

    @Column
    private String personInCharge;

    @Column(nullable = false)
    private String buyerCd;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private OrderHeaders.OrderStatus orderStatus;

    @Column
    private LocalDateTime orderDate;

    @Column
    private LocalDateTime requestDate;

    @OneToMany(mappedBy = "saleHistory", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<SaleHistoryItems> saleHistoryItems = new ArrayList<>();

}
