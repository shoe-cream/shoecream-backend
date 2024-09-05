package com.springboot.buyer_item.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.springboot.buyer.Buyer;
import com.springboot.item.entity.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BuyerItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int buyerItemId;

    @Column(nullable = false)
    private String buyerNm;

    @Column
    private String itemCd;

    @Column(nullable = false)
    private String itemNm;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private String unitPrice;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "buyerCd", referencedColumnName = "buyerCd")
    @JsonBackReference
    private Buyer buyer;

    @ManyToOne
    @JoinColumn(name = "itemId")
    @JsonBackReference
    private Item item;
}
