package com.springboot.buyer_item.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.springboot.buyer.entity.Buyer;
import com.springboot.item.entity.Item;
import lombok.AllArgsConstructor;
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
    private long buyerItemId;

    @Column(nullable = false)
    private String buyerNm;

    @Column
    private String buyerCd;

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

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus = ItemStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonBackReference
    private Item item;

    public void addBuyer(Buyer buyer) {
        this.buyer= buyer;
        if(!buyer.getBuyerItems().contains(this)) {
            buyer.getBuyerItems().add(this);
        }
    }

    @AllArgsConstructor
    public enum ItemStatus {
        ACTIVE ("활성 상태"),
        INACTIVE ("비활성 상태"),
        DELETED ("삭제 상태"),
        OUT_OF_STOCK ("품절 상태");

        @Setter
        @Getter
        private String statusDescription;
    }
}
