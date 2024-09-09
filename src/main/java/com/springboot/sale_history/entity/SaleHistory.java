package com.springboot.sale_history.entity;

import com.springboot.order_header.entity.OrderHeaders;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SaleHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long saleHistoryId;

    @Column
    private String employeeId;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderHeaders orderHeaders;
}
