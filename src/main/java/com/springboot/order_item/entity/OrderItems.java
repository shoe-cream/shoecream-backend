package com.springboot.order_item.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.springboot.order_header.entity.OrderHeaders;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 50)
    private String itemCD;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private OrderHeaders orderHeaders;
}
