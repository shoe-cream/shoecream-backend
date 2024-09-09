package com.springboot.manufacture.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.manufacture.entity.Manufacture;
import com.springboot.manufacture.mapper.ManufactureMapper;
import com.springboot.manufacture.repository.ManufactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ManufactureService {
    private final ManufactureRepository manufactureRepository;
    private final ManufactureMapper manufactureMapper;

    public void createManufacture(Manufacture manufacture) {
        verifyManufactureExists(manufacture.getMfId());
        manufactureRepository.save(manufacture);
    }

    public void deleteManufacture(Manufacture manufacture) {
        manufacture.setManufactureStatus(Manufacture.ManufactureStatus.INACTIVE);
        manufactureRepository.save(manufacture);
    }

    private void verifyManufactureExists(long mfId) {
        manufactureRepository.findById(mfId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));
    }
}
