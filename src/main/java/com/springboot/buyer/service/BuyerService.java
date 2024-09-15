package com.springboot.buyer.service;

import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.repository.BuyerRepository;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.buyer_item.repository.BuyerItemRepository;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
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
public class BuyerService {
    private final BuyerRepository buyerRepository;
    private final MemberRepository memberRepository;

    //Buyer 생성
    public void createBuyer(List<Buyer> buyers, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        buyers.stream().forEach(buyer -> {
            verifyExistName(buyer.getBuyerNm());
            verifyExistTel(buyer.getTel());
            verifyExistEmail(buyer.getEmail());
            verifyBuyerCdExists(buyer.getBuyerCd());
            buyerRepository.save(buyer);
        });
    }

    // 바이어의 이름, 코드, 타입중 하나로 검색
    public Buyer findBuyerByFilter(String buyerCd, String buyerNm, String businessType, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        if((buyerCd != null && !buyerCd.isEmpty()) ||
            buyerNm != null && !buyerNm.isEmpty() ||
            businessType != null && !businessType.isEmpty()) {

           return buyerRepository.findByBuyerCdOrBuyerNmOrBusinessTypeAndBuyerStatusNot(buyerCd, buyerNm, businessType,  Buyer.BuyerStatus.INACTIVE)
                   .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CONDITION_NOT_FIT));
        } else {
                throw new BusinessLogicException(ExceptionCode.AT_LEAST_ONE_CONDITION);
        }
    }

    // 페이지네이션으로 필터링을통해 전체조회 ok 필터링 없이 전체를 조회 ok.
    public Page<Buyer> findBuyers(int page, int size, String criteria, String direction, String businessType, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = createPageable(page, size, criteria, direction);

        if (businessType != null && !businessType.isEmpty()) {
            return buyerRepository.findAllByBusinessTypeAndBuyerStatusNot(businessType, Buyer.BuyerStatus.INACTIVE, pageable);
        } else {
            return buyerRepository.findAllByBuyerStatusNot(Buyer.BuyerStatus.INACTIVE, pageable);
        }
    }

    //Pageable 생성
    private Pageable createPageable(int page, int size, String sortCriteria, String direction) {

        Sort.Direction sortDirection = (direction == null || direction.isEmpty()) ? Sort.Direction.DESC : Sort.Direction.fromString(direction);

        Sort sort = Sort.by(sortDirection, sortCriteria);

        return PageRequest.of(page, size, sort);
    }

    //buyerNm + buyerCd 조합으로 조회
    public Buyer findBuyerWithItems(String buyerCd, String buyerNm) {
        if ((buyerCd == null || buyerCd.isEmpty()) && (buyerNm == null || buyerNm.isEmpty())) {
            throw new BusinessLogicException(ExceptionCode.CONDITION_NOT_FIT);
        }

        Buyer buyer = buyerRepository.findByBuyerCdOrBuyerNmAndBuyerStatusNot(buyerCd, buyerNm, Buyer.BuyerStatus.INACTIVE )
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));

        return buyer;
    }

    //Buyer 정보 수정 - 이름/주소/이메일/연락처/사업자유형/상태
    public Buyer updateBuyer(Buyer buyer, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Buyer findBuyer = buyerRepository.findById(buyer.getBuyerId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));

        Optional.ofNullable(buyer.getBuyerNm())
                .ifPresent(buyerNm -> {

                        verifyExistName(buyerNm);
                        findBuyer.setBuyerNm(buyerNm);
                });

        Optional.ofNullable(buyer.getAddress())
                .ifPresent(findBuyer::setAddress);

        Optional.ofNullable(buyer.getEmail())
                .ifPresent(email -> {

                    verifyExistEmail(email);
                    findBuyer.setEmail(email);
                });

        Optional.ofNullable(buyer.getTel())
                .ifPresent(tel -> {

                    verifyExistTel(tel);
                    findBuyer.setTel(tel);
                });

        Optional.ofNullable(buyer.getBusinessType())
                .ifPresent(findBuyer::setBusinessType);

        Optional.ofNullable(buyer.getBuyerStatus())
                .ifPresent(status -> {
                    if(status.equals(Buyer.BuyerStatus.INACTIVE)) {
                        throw new BusinessLogicException(ExceptionCode.INVALID_REQUEST);
                    }

                    findBuyer.setBuyerStatus(status);
                });

        findBuyer.setModifiedAt(LocalDateTime.now());

        return buyerRepository.save(findBuyer);
    }


    //Buyer 삭제
    public void deleteBuyer(Long buyerId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Buyer buyer = findVerifiedBuyer(buyerId);
        buyer.setBuyerStatus(Buyer.BuyerStatus.INACTIVE);

        buyerRepository.save(buyer);
    }

    //BuyerCd 중복검사
    private void verifyBuyerCdExists(String buyerCd) {
        Optional<Buyer> buyer = buyerRepository.findByBuyerCd(buyerCd);

        if(buyer.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.BUYER_CD_ALREADY_EXIST);
        }
    }

    //Buyer-email 중복검사
    private void verifyExistEmail(String email) {
        Optional<Buyer> buyer = buyerRepository.findByEmail(email);
        if(buyer.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_ALREADY_EXISTS);
        }
    }

    //Buyer-name 중복검사
    private void verifyExistName(String name) {
        Optional<Buyer> buyer = buyerRepository.findByBuyerNm(name);
        if(buyer.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.BUYER_ALREADY_EXIST);
        }
    }

    //Buyer-tel 중복검사
    private void verifyExistTel(String tel) {
        if(buyerRepository.existsByTel(tel)) {
            throw new BusinessLogicException(ExceptionCode.BUYER_ALREADY_EXIST);
        }
    }

    //buyerId를 통해 Buyer 검증
    private Buyer findVerifiedBuyer(Long buyerId) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));

        //isDeleted(buyer);

        return buyer;
    }

    //buyerCd를 통해 Buyer 검증
    public Buyer findVerifiedBuyer(String buyerCd) {
        Buyer buyer = buyerRepository.findByBuyerCd(buyerCd)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));

       // isDeleted(buyer);

        return buyer;
    }

    //검증된 member 정보 가져오기
    private Member extractMemberFromAuthentication(Authentication authentication) {
        String username = (String) authentication.getPrincipal();

        return memberRepository.findByEmployeeId(username)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    //고객사 전체 조회
    public List<Buyer> findAllActiveBuyers() {
        return buyerRepository.findAllByBuyerStatusNot(Buyer.BuyerStatus.INACTIVE);
    }

    public void isDeleted (Buyer buyer) {
        if(buyer.getBuyerStatus().equals(Buyer.BuyerStatus.INACTIVE)) {
            throw new BusinessLogicException(ExceptionCode.INACTIVE_STATUS);
        }
    }

    public Buyer findBuyer(String buyerCd, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Buyer buyer = findVerifiedBuyer(buyerCd);
        isDeleted(buyer);

        return buyer;
    }
}
