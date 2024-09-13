package com.springboot.manufacture.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.manufacture.entity.Manufacture;
import com.springboot.manufacture.repository.ManufactureRepository;
import com.springboot.manufacture_history.entity.ManuFactureHistory;
import com.springboot.manufacture_history.repository.ManufactureHistoryRepository;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ManufactureService {
    private final ManufactureRepository manufactureRepository;
    private final MemberRepository memberRepository;
    private final ManufactureHistoryRepository manufactureHistoryRepository;

    public void createManufacture(List<Manufacture> manufactures, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        manufactures.stream().forEach(manufacture -> {
            verifyManufactureCdExists(manufacture.getMfCd());
            verifyManufactureNmExists(manufacture.getMfNm());
            verifyExistsEmail(manufacture.getEmail());
            manufactureRepository.save(manufacture);
        });
    }

    //manufacture
    public Manufacture findManufacture(String mfCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        return findVerifiedManufactureByMfCd(mfCd);

    }

    public Page<Manufacture> getManufactures(String sortBy, int page, int size, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable;

        if ("mfCd".equals(sortBy)) {
            pageable = PageRequest.of(page, size, Sort.by("mfCd").ascending());
        } else if ("region".equals(sortBy)) {
            pageable = PageRequest.of(page, size, Sort.by("region").ascending());
        } else {
            // 기본 정렬: 최신순
            pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        }

       return manufactureRepository.findAllByManufactureStatus(Manufacture.ManufactureStatus.ACTIVE, pageable);
    }

    public Manufacture updateManufacture(Manufacture manufacture, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Manufacture findManufacture = verifyManufacture(manufacture.getMfId());

        Optional.ofNullable(manufacture.getEmail())
                .ifPresent(email -> {
                    if(!email.equals(findManufacture.getEmail())) {
                        verifyExistsEmail(email);
                        findManufacture.setEmail(email);
                    }
                });

        Optional.ofNullable(manufacture.getRegion())
                .ifPresent(region -> findManufacture.setRegion(region));
        Optional.ofNullable(manufacture.getMfNm())
                .ifPresent(mfNm -> {
                    if(!mfNm.equals(findManufacture.getMfNm())) {
                        verifyManufactureNmExists(mfNm);
                        findManufacture.setMfNm(mfNm);
                    }
                });

        findManufacture.setModifiedAt(LocalDateTime.now());

        return manufactureRepository.save(findManufacture);
    }

    // mfId로 manufacture 삭제
    public void deleteManufactures(Long mfId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Manufacture manufacture = verifyManufacture(mfId);
        manufacture.setManufactureStatus(Manufacture.ManufactureStatus.INACTIVE);

        manufactureRepository.save(manufacture);
    }

    //mfCd로 manufacture 삭제
    public void deleteManufacture(String mfCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Manufacture manufacture = findVerifiedManufactureByMfCd(mfCd);
        manufacture.setManufactureStatus(Manufacture.ManufactureStatus.INACTIVE);
        manufactureRepository.save(manufacture);
    }

    // mfId로 Manufacture 검증
    public Manufacture verifyManufacture(long mfId) {
        return manufactureRepository.findById(mfId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));

    }

    // mfCd로 Manufacture 검증
    private Manufacture findVerifiedManufactureByMfCd(String mfCd) {

        return manufactureRepository.findBymfCd(mfCd)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));
    }

    public Page<ManuFactureHistory> findManufactureHistories (int page, int size,
                                                              String sort,
                                                              String direction,
                                                              String mfCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = createPageable(page, size, sort, direction);
        return manufactureHistoryRepository.findByMfCd(mfCd, pageable);
    }

    private Pageable createPageable(int page, int size, String sortCriteria, String direction) {

        Sort.Direction sortDirection = (direction == null || direction.isEmpty()) ? Sort.Direction.DESC : Sort.Direction.fromString(direction);

        Sort sort = Sort.by(sortDirection, sortCriteria);

        return PageRequest.of(page, size, sort);
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String username = (String) authentication.getPrincipal();

        return memberRepository.findByEmployeeId(username)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    //이메일 중복 검사
    private void verifyExistsEmail(String email) {
        if (manufactureRepository.existsByEmail(email)) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_ALREADY_EXISTS);
        }
    }

    //MF Code 중복 검사
    private void verifyManufactureCdExists(String mfCd) {
        if(manufactureRepository.existsByMfCd(mfCd)) {
            throw new BusinessLogicException(ExceptionCode.MANUFACTURE_CODE_EXIST);
        }
    }

    //MF name 중복 검사
    private void verifyManufactureNmExists(String mfNm) {
        if(manufactureRepository.existsByMfNm(mfNm)) {
            throw new BusinessLogicException(ExceptionCode.MANUFACTURE_NAME_EXIST);
        }
    }

}
