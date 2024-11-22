package com.springboot.manufacture_item.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.springboot.item.entity.Item;
import com.springboot.manufacture.entity.Manufacture;
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
public class ItemManufacture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long mfItemId;

    @Column
    private BigDecimal unitPrice;

    @Column
    private Integer qty;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime modifiedAt = LocalDateTime.now();

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
