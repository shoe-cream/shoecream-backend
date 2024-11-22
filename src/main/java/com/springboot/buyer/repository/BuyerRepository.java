package com.springboot.buyer.repository;

import com.springboot.buyer.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    Optional<Buyer> findByBuyerCd(String buyerCd);

    Optional<Buyer> findByEmail(String email);

    Optional<Buyer> findByBuyerNm(String name);

    Boolean existsByTel(String tel);

    List<Buyer> findAllByBuyerStatusNot(Buyer.BuyerStatus buyerStatus);
}
