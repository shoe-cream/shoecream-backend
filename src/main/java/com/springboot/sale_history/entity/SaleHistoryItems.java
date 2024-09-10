package com.springboot.sale_history.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.springboot.order_item.entity.OrderItems;
import lombok.Builder;
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
public class SaleHistoryItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long saleHistoryItemId;

    @ManyToOne
    @JoinColumn(name = "sale_history_id")
    @JsonBackReference
    private SaleHistory saleHistory;

    @Column(nullable = false)
    private String itemCD;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    public SaleHistoryItems(OrderItems orderItems) {
        this.itemCD = orderItems.getItemCD();
        this.quantity = orderItems.getQuantity();
        this.unitPrice = orderItems.getUnitPrice();
        this.unit = orderItems.getUnit();
        this.startDate = orderItems.getStartDate();
        this.endDate = orderItems.getEndDate();
    }
}
