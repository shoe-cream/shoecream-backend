package com.springboot.manufacture.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springboot.item_manufacture.entity.ItemManufacture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private String region;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String mfCd;

    @Column(nullable = false)
    private String mfNm;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

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
