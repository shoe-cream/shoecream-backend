package com.springboot.manufacture.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springboot.item_manufacture.entity.ItemManufacture;
import lombok.AllArgsConstructor;
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
    private long mfId;

    @Column
    private String region;

    @Column
    private String email;

    @Enumerated(EnumType.STRING)
    private ManufactureStatus manufactureStatus = ManufactureStatus.ACTIVE;

    @OneToMany(mappedBy = "manufacture")
    @JsonManagedReference
    private List<ItemManufacture> itemManufactures = new ArrayList<>();

    @AllArgsConstructor
    public enum ManufactureStatus {
        ACTIVE ("활성 상태"),
        INACTIVE ("비활성 상태");

        @Setter
        @Getter
        private String statusDescription;
    }
}
