package com.springboot.manufacture.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springboot.item_manufacture.entity.ItemManufacture;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Manufacture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mfId;

    @Column
    private String region;

    @Column
    private String email;

    @OneToMany(mappedBy = "manufacture", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ItemManufacture> itemManufactures = new ArrayList<>();
}
