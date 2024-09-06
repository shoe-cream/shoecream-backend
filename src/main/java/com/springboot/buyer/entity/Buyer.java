package com.springboot.buyer.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.order_header.entity.OrderHeaders;
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
public class Buyer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long buyerId;

    @Column(unique = true, nullable = false)
    private String buyerCd;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String buyerNm;

    @Column(unique = true, nullable = false)
    private String tel;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String businessType;

    @Enumerated(EnumType.STRING)
    private BuyerStatus buyerStatus = BuyerStatus.ACTIVE;

    @OneToMany(mappedBy = "buyer")
    @JsonManagedReference
    private List<BuyerItem> buyerItems = new ArrayList<>();

    @OneToMany(mappedBy = "buyer")
    @JsonManagedReference
    private List<OrderHeaders> orderHeaders = new ArrayList<>();

    @AllArgsConstructor
    public enum BuyerStatus {
        ACTIVE ("활성 상태"),
        INACTIVE ("비활성 상태"),
        SUSPENDED ("거래 중단"),
        TERMINATED ("계약 해지");

        @Getter
        @Setter
        private String statusDescription;
    }
}
