package com.springboot.buyer.repository;

import com.springboot.buyer.entity.Buyer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    Optional<Buyer> findByBuyerCd(String buyerCd);
    Optional<Buyer> findByBuyerCdOrBuyerNmOrBusinessType(String buyerCd, String buyerNm, String businessType);
    Optional<Buyer> findByBuyerCdOrBuyerNm(String buyerCd, String buyerNm);
    Optional<Buyer> findByEmail(String email);
    Optional<Buyer> findByTel(String tel);
    Page<Buyer> findAll(Pageable pageable);
    Page<Buyer> findAllByBusinessType(String businessType, Pageable pageable);

    Optional<Buyer> findByBuyerNm(String name);

    Boolean existsByTel(String tel);

    Page<Buyer> findAllByBuyerStatusNot(Buyer.BuyerStatus buyerStatus, Pageable pageable);
    Page<Buyer> findAllByBusinessTypeAndBuyerStatusNot(String businessType, Buyer.BuyerStatus buyerStatus, Pageable pageable);
}
