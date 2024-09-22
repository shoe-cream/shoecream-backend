package com.springboot.manufacture.repository;

import com.springboot.manufacture.entity.Manufacture;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ManufactureRepository extends JpaRepository<Manufacture, Long> {

    boolean existsByMfCd(String mfCd);
    boolean existsByMfNm(String mfNm);
    boolean existsByEmail(String email);

    Optional<Manufacture> findByMfCd(String mfCd);
    Optional<Manufacture> findByMfNm(String mfNm);

    List<Manufacture> findAllByManufactureStatusNot(Manufacture.ManufactureStatus manufactureStatus);
}

