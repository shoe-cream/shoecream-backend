package com.springboot.manufacture_history.entity;

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
public class ManuFactureHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long mfHistoryId;

    @Column
    private long mfId;

    @Column
    private String itemCd;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column
    private int qty;

    @Column
    private LocalDateTime receiveDate;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

}
