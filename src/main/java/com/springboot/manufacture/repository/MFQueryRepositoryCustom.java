package com.springboot.manufacture.repository;

import com.springboot.manufacture.entity.Manufacture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MFQueryRepositoryCustom {
    Page<Manufacture> findManufactures(String mfCd, String mfNm, String email, String region, Pageable pageable);
}
