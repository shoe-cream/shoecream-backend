package com.springboot.buyer_item.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.springboot.buyer.entity.Buyer;
import com.springboot.item.entity.Item;
import lombok.AllArgsConstructor;
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
public class BuyerItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long buyerItemId;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    public void addBuyer(Buyer buyer) {
        this.buyer= buyer;
        if(!buyer.getBuyerItems().contains(this)) {
            buyer.getBuyerItems().add(this);
        }
    }

    public void addItem(Item item) {
        this.item = item;
        if(!item.getBuyerItems().contains(this)) {
            item.getBuyerItems().add(this);
        }
    }
}
