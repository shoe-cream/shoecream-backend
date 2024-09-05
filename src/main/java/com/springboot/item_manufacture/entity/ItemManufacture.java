package com.springboot.item_manufacture.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.springboot.item.entity.Item;
import com.springboot.manufacture.entity.Manufacture;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ItemManufacture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mfItemId;

    @Column
    private int unitPrice;

    @Column
    private int qty;

    @ManyToOne
    @JoinColumn(name = "mfId")
    @JsonBackReference
    private Manufacture manufacture;

    @ManyToOne
    @JoinColumn(name = "itemId")
    @JsonBackReference
    private Item item;
}
