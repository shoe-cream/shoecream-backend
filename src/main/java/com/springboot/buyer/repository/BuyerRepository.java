package com.springboot.buyer.repository;

import com.springboot.buyer.entity.Buyer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    Optional<Buyer> findByBuyerCd(String buyerCd);
    Optional<Buyer> findByEmail(String email);
    Page<Buyer> findAll(Pageable pageable);
    Optional<Buyer> findByBuyerNm(String name);
    Boolean existsByTel(String tel);
    List<Buyer> findAllByBuyerStatusNot(Buyer.BuyerStatus buyerStatus);
    Page<Buyer> findAllByBuyerStatusNot(Buyer.BuyerStatus buyerStatus, Pageable pageable);

    Optional<Buyer> findByBuyerCdOrBuyerNmAndBuyerStatusNot(String buyerCd, String buyerNm, Buyer.BuyerStatus buyerStatus);

    @Query("SELECT b FROM Buyer b WHERE REPLACE(LOWER(b.buyerNm), ' ', '') = :buyerNm AND b.buyerStatus <> :status")
    Page<Buyer> findByBuyerNmIgnoreCaseWithoutSpacesAndBuyerStatusNot(
            @Param("buyerNm") String buyerNm, @Param("status") Buyer.BuyerStatus status, Pageable pageable);
}
