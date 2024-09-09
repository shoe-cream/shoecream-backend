package com.springboot.item_manufacture.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.springboot.buyer.entity.Buyer;
import com.springboot.item.entity.Item;
import com.springboot.manufacture.entity.Manufacture;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ItemManufacture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long mfItemId;

    @Column
    private BigDecimal unitPrice;

    @Column
    private int qty;

    @ManyToOne
    @JoinColumn(name = "mf_id")
    @JsonBackReference
    private Manufacture manufacture;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonBackReference
    private Item item;

    public void addMf(Manufacture manufacture) {
        this.manufacture = manufacture;
        if(!manufacture.getItemManufactures().contains(this)) {
            manufacture.getItemManufactures().add(this);
        }
    }

    public void addItem(Item item) {
        this.item = item;
        if(!item.getItemManufactures().contains(this)) {
            item.getItemManufactures().add(this);
        }
    }

}
