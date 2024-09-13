package com.springboot.buyer.controller;

import com.springboot.buyer.dto.Dto;
import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.mapper.BuyerMapper;
import com.springboot.buyer.service.BuyerService;
import com.springboot.buyer_item.mapper.BuyerItemMapper;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buyers")
@RequiredArgsConstructor
@Validated
@Transactional
public class BuyerController {
    private final BuyerService buyerService;
    private final BuyerMapper buyerMapper;
    private final BuyerItemMapper buyerItemMapper;

    //Buyer 등록
    @PostMapping
    public ResponseEntity postBuyer(@Valid @RequestBody List<Dto.BuyerPostDto> postDtos, Authentication authentication) {
        buyerService.createBuyer(buyerMapper.postDtosToBuyerItems(postDtos), authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 특정 바이어를, 바이어이름, 바이어코드, 바이어의 사업유형으로 조회 (없으면 예외처리)
    @GetMapping("/search")
    public ResponseEntity getBuyer(@RequestParam(required = false) String buyerCd,
                                   @RequestParam(required = false) String buyerNm,
                                   @RequestParam(required = false) String businessType,
                                   Authentication authentication) {

        Buyer buyerByCd = buyerService.findBuyerByFilter(buyerCd, buyerNm, businessType, authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(buyerMapper.buyerToBuyerResponseDto(buyerByCd)), HttpStatus.OK);
    }

    // 바이어와 바이어아이템을 함께 조회
    @GetMapping("/search/items")
    public ResponseEntity getBuyerWithItems(@RequestParam(required = false) String buyerCd,
                                            @RequestParam(required = false) String buyerNm) {
        Buyer buyer = buyerService.findBuyerWithItems(buyerCd, buyerNm);

        Dto.BuyerResponseWithItemDto response =
                buyerMapper.buyerItemsToBuyerResponseWithItems(buyer);


        return new ResponseEntity<>(
                new SingleResponseDto<>(response), HttpStatus.OK);
    }

    // 페이지네이션으로 바이어 전체조회 또는 사업유형으로 전체조회.
    @GetMapping
    public ResponseEntity getBuyers(@RequestParam @Positive int page,
                                    @RequestParam @Positive int size,
                                    @RequestParam(required = false) String sort,
                                    @RequestParam(required = false) String businessType,
                                    @RequestParam(required = false) String direction,
                                    Authentication authentication) {

        String criteria = "buyerId";

        if(sort != null) {

            //정렬 기준
            List<String> sorts = Arrays.asList("buyerId", "buyerNm", "createdAt", "buyerCd", "address", "businessType");

            if(sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<Buyer> buyerPage = buyerService.findBuyers(page -1, size, criteria, direction, businessType, authentication);
        List<Buyer> buyers = buyerPage.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(buyerMapper.buyerToBuyerResponseDtos(buyers), buyerPage), HttpStatus.OK);
    }

    //Buyer 전체 조회
    @GetMapping("/all")
    public ResponseEntity getAllBuyers() {
        List<Buyer> buyers = buyerService.findAll();

        List<Buyer> sortedBuyers = buyers.stream()
                .sorted(Comparator.comparing(Buyer::getBuyerNm))
                .collect(Collectors.toList());

        return new ResponseEntity<>(buyerMapper.buyerToBuyerResponseDtos(sortedBuyers), HttpStatus.OK);
    }

    //Buyer 수정
    @PatchMapping
    public ResponseEntity patchBuyer(@RequestBody @Valid List<Dto.BuyerPatchDto> patchDtos,
                                     Authentication authentication) {
        List<Buyer> buyers = new ArrayList<>();
        for(Dto.BuyerPatchDto patchDto : patchDtos) {
            Buyer buyer = buyerService.updateBuyer(buyerMapper.buyerPatchDtoToBuyer(patchDto), authentication);
            buyers.add(buyer);
        }

        return new ResponseEntity<>(
                new SingleResponseDto<>(buyerMapper.buyerToBuyerResponseDtos(buyers)), HttpStatus.OK);
    }

    // Buyer 다중 id 선택 - 삭제
    @DeleteMapping
    public ResponseEntity deleteBuyers (@RequestBody Dto.BuyerDeleteDtos deleteDtos,
                                        Authentication authentication) {
        List<Long> itemIds = deleteDtos.getBuyerId();
        for(Long id : itemIds) {
            buyerService.deleteBuyer(id, authentication);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}