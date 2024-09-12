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

           return buyerRepository.findByBuyerCdOrBuyerNmOrBusinessType(buyerCd, buyerNm, businessType)
                   .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CONDITION_NOT_FIT));
        } else {
                throw new BusinessLogicException(ExceptionCode.AT_LEAST_ONE_CONDITION);
        }
    }

    // 페이지네이션으로 필터링을통해 전체조회 ok 필터링 없이 전체를 조회 ok.
    public Page<Buyer> findBuyers(int page, int size, String criteria, String businessType, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Pageable pageable = createPageable(page, size, criteria);

        if (businessType != null && !businessType.isEmpty()) {
            return buyerRepository.findAllByBusinessType(businessType, pageable);
        } else {
            return buyerRepository.findAll(pageable);
        }
    }

    private Pageable createPageable(int page, int size, String sortCriteria) {
        Sort sort = Sort.by(sortCriteria).descending();

        return PageRequest.of(page, size, sort);
    }

    public Buyer findBuyerWithItems(String buyerCd, String buyerNm) {
        if ((buyerCd == null || buyerCd.isEmpty()) && (buyerNm == null || buyerNm.isEmpty())) {
            throw new BusinessLogicException(ExceptionCode.CONDITION_NOT_FIT);
        }

        Buyer buyer = buyerRepository.findByBuyerCdOrBuyerNm(buyerCd, buyerNm)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));

        return buyer;
    }

    public Buyer updateBuyer(Buyer buyer, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Buyer findBuyer = buyerRepository.findById(buyer.getBuyerId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));

        Optional.ofNullable(buyer.getBuyerNm())
                .ifPresent(buyerNm -> {
                    if(!buyerNm.equals(findBuyer.getBuyerNm())) {
                        verifyExistName(buyerNm);
                        findBuyer.setBuyerNm(buyerNm);
                    }
                });

        Optional.ofNullable(buyer.getAddress())
                .ifPresent(address -> findBuyer.setAddress(address));

        Optional.ofNullable(buyer.getEmail())
                .ifPresent(email -> {
                    if(!email.equals(findBuyer.getEmail())) {
                        verifyExistEmail(email);
                        findBuyer.setEmail(email);
                    }
                });

        Optional.ofNullable(buyer.getTel())
                .ifPresent(tel -> {
                    if(!tel.equals(findBuyer.getTel())) {
                        verifyExistTel(tel);
                        findBuyer.setTel(tel);
                    }
                });

        Optional.ofNullable(buyer.getBusinessType())
                .ifPresent(businessType -> findBuyer.setBusinessType(businessType));

        findBuyer.setModifiedAt(LocalDateTime.now());

        return buyerRepository.save(findBuyer);
    }

    public void deleteBuyer(long buyerId, Authentication authentication) {
        extractMemberFromAuthentication(authentication);

        Optional<Buyer> findBuyer = buyerRepository.findById(buyerId);
        Buyer buyer = findBuyer
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));
        buyer.setBuyerStatus(Buyer.BuyerStatus.INACTIVE);
        buyerRepository.save(buyer);
    }

    private void verifyBuyerCdExists(String buyerCd) {
        Optional<Buyer> buyer = buyerRepository.findByBuyerCd(buyerCd);
        if(buyer.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.BUYER_CD_ALREADY_EXIST);
        }
    }

    private void verifyExistEmail(String email) {
        Optional<Buyer> buyer = buyerRepository.findByEmail(email);
        if(buyer.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private void verifyExistName(String name) {
        Optional<Buyer> buyer = buyerRepository.findByBuyerNm(name);
        if(buyer.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.BUYER_ALREADY_EXIST);
        }
    }

    private void verifyExistTel(String tel) {
        if(buyerRepository.existsByTel(tel)) {
            throw new BusinessLogicException(ExceptionCode.BUYER_ALREADY_EXIST);
        }
    }

    public Buyer findVerifiedBuyer(String buyerCd) {
        return buyerRepository.findByBuyerCd(buyerCd)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BUYER_NOT_FOUND));
    }

    private Member extractMemberFromAuthentication(Authentication authentication) {
        String username = (String) authentication.getPrincipal();

        return memberRepository.findByEmployeeId(username)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

}
