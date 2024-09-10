package com.springboot.manufacture.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.manufacture.entity.Manufacture;
import com.springboot.manufacture.mapper.ManufactureMapper;
import com.springboot.manufacture.repository.ManufactureRepository;
import com.springboot.manufacture_history.entity.ManuFactureHistory;
import com.springboot.manufacture_history.repository.ManufactureHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ManufactureService {
    private final ManufactureRepository manufactureRepository;
    private final ManufactureHistoryRepository manufactureHistoryRepository;

    public void createManufacture(Manufacture manufacture) {
        verifyManufactureExists(manufacture.getMfId());
        manufactureRepository.save(manufacture);
    }

    public Manufacture getManufacture(long mfId) {
        Manufacture manufacture = verifyManufacture(mfId);

        return manufacture;
    }

    public Page<Manufacture> getManufactures(String sortBy, int page, int size) {
        Pageable pageable;

        if ("mfCd".equals(sortBy)) {
            pageable = PageRequest.of(page, size, Sort.by("mfCd").ascending());
        } else if ("region".equals(sortBy)) {
            pageable = PageRequest.of(page, size, Sort.by("region").ascending());
        } else {
            // 기본 정렬: 최신순
            pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        }

        return manufactureRepository.findAll(pageable);
    }

    public Manufacture updateManufacture(Manufacture manufacture) {
        Manufacture findManufacture = verifyManufacture(manufacture.getMfId());

        Optional.ofNullable(manufacture.getEmail())
                .ifPresent(email -> findManufacture.setEmail(email));
        Optional.ofNullable(manufacture.getRegion())
                .ifPresent(region -> findManufacture.setRegion(region));
        Optional.ofNullable(manufacture.getMfNm())
                .ifPresent(mfNm -> findManufacture.setMfNm(mfNm));

        return manufactureRepository.save(findManufacture);
    }


    public void deleteManufacture(long mfId) {
        Optional<Manufacture> optionalManufacture = manufactureRepository.findById(mfId);
        Manufacture manufacture = optionalManufacture
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));
        manufacture.setManufactureStatus(Manufacture.ManufactureStatus.INACTIVE);
        manufactureRepository.save(manufacture);
    }

    private Manufacture verifyManufacture(long mfId) {
       Manufacture manufacture = manufactureRepository.findById(mfId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));

        return manufacture;
    }

    private void verifyManufactureExists(long mfId) {
        Optional<Manufacture> optionalManufacture = manufactureRepository.findById(mfId);
        if(optionalManufacture.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.MANUFACTURE_EXIST);
        }
    }

    public Page<ManuFactureHistory> findManufactureHistories (int page, int size, Long mfId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return manufactureHistoryRepository.findByMfId(mfId, pageable);
    }

}
