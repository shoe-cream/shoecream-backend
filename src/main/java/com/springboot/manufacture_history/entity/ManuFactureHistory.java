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
    private Long mfHistoryId;

    @Column(nullable = false)
    private Long mfItemId;

    @Column(nullable = false)
    private Long mfId;

    @Column(nullable = false)
    private String mfCd;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String itemCd;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int qty;

    @Column(nullable = false)
    private LocalDateTime receiveDate;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

}
