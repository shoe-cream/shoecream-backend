package com.springboot.buyer.repository;

import com.springboot.buyer.entity.Buyer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuyerQueryRepositoryCustom {
    Page<Buyer> findBuyer(String buyerNm,
                          String buyerCd,
                          String tel,
                          String address,
                          String businessType,
                          Pageable pageable);
}
