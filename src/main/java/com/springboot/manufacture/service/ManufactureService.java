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

    //제조사 등록
    public void createManufacture(List<Manufacture> manufactures, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        manufactures.stream().forEach(manufacture -> {

            verifyManufactureCdExists(manufacture.getMfCd());
            verifyManufactureNmExists(manufacture.getMfNm());
            verifyExistsEmail(manufacture.getEmail());

            manufactureRepository.save(manufacture);
        });
    }

    //제조사 개별 조회 (mfCd를 통해)
    public Manufacture findManufacture(String mfCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Manufacture manufacture = findVerifiedManufactureByMfCd(mfCd);
        isDeleted(manufacture);

        return manufacture;
    }

    //제조사 전체 조회 (pagination)
    public Page<Manufacture> getManufactures(String sortBy, String direction, int page, int size, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = createPageable(page, size, sortBy, direction);

       return manufactureRepository.findAllByManufactureStatusNot(Manufacture.ManufactureStatus.INACTIVE, pageable);
    }

    //제조사 정보 수정 (이메일/ 지역/ 이름)
    public Manufacture updateManufacture(Manufacture manufacture, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Manufacture findManufacture = verifyManufacture(manufacture.getMfId());

        Optional.ofNullable(manufacture.getEmail())
                .ifPresent(email -> {
                    verifyExistsEmail(email);
                    findManufacture.setEmail(email);

                });

        Optional.ofNullable(manufacture.getRegion())
                .ifPresent(findManufacture::setRegion);

        Optional.ofNullable(manufacture.getMfNm())
                .ifPresent(mfNm -> {
                    verifyManufactureNmExists(mfNm);
                    findManufacture.setMfNm(mfNm);

                });

        Optional.ofNullable(manufacture.getManufactureStatus())
                .ifPresent(status-> {
                    if(status.equals(Manufacture.ManufactureStatus.INACTIVE)) {
                        throw new BusinessLogicException(ExceptionCode.INVALID_REQUEST);
                    }

                    findManufacture.setManufactureStatus(status);
                });

        findManufacture.setModifiedAt(LocalDateTime.now());

        return manufactureRepository.save(findManufacture);
    }

    // mfId로 manufacture DB에서 삭제 -> 여러 개 삭제시 사용
    public void deleteManufactures(Long mfId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Manufacture manufacture = verifyManufacture(mfId);
        manufacture.setManufactureStatus(Manufacture.ManufactureStatus.INACTIVE);

        manufactureRepository.save(manufacture);
    }

    //mfCd로 manufacture DB에서 삭제
    public void deleteManufacture(String mfCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Manufacture manufacture = findVerifiedManufactureByMfCd(mfCd);
        manufacture.setManufactureStatus(Manufacture.ManufactureStatus.INACTIVE);

        manufactureRepository.save(manufacture);
    }

    //ManuFacture-History 조회 (mfCd로 필터 , pagination)
    public Page<ManuFactureHistory> findManufactureHistories (int page, int size,
                                                              String sort,
                                                              String direction,
                                                              String mfCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = createPageable(page, size, sort, direction);
        return manufactureHistoryRepository.findByMfCd(mfCd, pageable);
    }

    // 제조사 전체 조회
    public List<Manufacture> findManufactureAll() {
        return manufactureRepository.findAllByManufactureStatusNot(Manufacture.ManufactureStatus.INACTIVE);
    }

    //Pageable 생성
    private Pageable createPageable(int page, int size, String sortCriteria, String direction) {

        Sort.Direction sortDirection = (direction == null || direction.isEmpty()) ? Sort.Direction.DESC : Sort.Direction.fromString(direction);

        Sort sort = Sort.by(sortDirection, sortCriteria);

        return PageRequest.of(page, size, sort);
    }


    // mfId로 Manufacture 검증
    public Manufacture verifyManufacture(long mfId) {
        return manufactureRepository.findById(mfId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));

    }

    // mfNm로 Manufacture 검증
    public Manufacture verifyManufactureByNm(String mfNm) {
        Manufacture manufacture = manufactureRepository.findByMfNm(mfNm)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));

        isDeleted(manufacture);
        return manufacture;
    }

    // mfCd로 Manufacture 검증
    private Manufacture findVerifiedManufactureByMfCd(String mfCd) {

        return manufactureRepository.findByMfCd(mfCd)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MANUFACTURE_NOT_FOUND));
    }

    //Member 정보 가져오기
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

    public void isDeleted (Manufacture manufacture) {
        if(manufacture.getManufactureStatus().equals(Manufacture.ManufactureStatus.INACTIVE)) {
            throw new BusinessLogicException(ExceptionCode.INACTIVE_STATUS);
        }
    }
}
