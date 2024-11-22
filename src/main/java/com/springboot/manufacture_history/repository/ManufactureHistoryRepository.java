package com.springboot.manufacture_history.repository;

import com.springboot.manufacture_history.entity.ManuFactureHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufactureHistoryRepository extends JpaRepository<ManuFactureHistory, Long> {

    Page<ManuFactureHistory> findByMfItemId(Long mfItemId, Pageable pageable);
    Page<ManuFactureHistory> findByMfCd(String mfCd, Pageable pageable);
}
