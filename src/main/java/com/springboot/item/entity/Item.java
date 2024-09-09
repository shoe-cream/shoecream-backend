package com.springboot.item.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.item_manufacture.entity.ItemManufacture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long itemId;

    @Column
    private String itemCd;

    @Column(nullable = false)
    private String itemNm;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private int size;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus = ItemStatus.ON_SALE;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "item")
    @JsonManagedReference
    private List<ItemManufacture> itemManufactures = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    @JsonManagedReference
    private List<BuyerItem> buyerItems = new ArrayList<>();

    @AllArgsConstructor
    public enum ItemStatus {
        ON_SALE("판매중"),
        NOT_FOR_SALE("판매중지");

        @Getter
        @Setter
        private String status;
    }
}
