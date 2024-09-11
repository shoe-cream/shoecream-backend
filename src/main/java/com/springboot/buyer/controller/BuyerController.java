package com.springboot.buyer.controller;

import com.springboot.buyer.dto.Dto;
import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.mapper.BuyerMapper;
import com.springboot.buyer.service.BuyerService;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.buyer_item.mapper.BuyerItemMapper;
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
import java.util.List;

@RestController
@RequestMapping("/buyers")
@RequiredArgsConstructor
@Validated
@Transactional
public class BuyerController {
    private final BuyerService buyerService;
    private final BuyerMapper buyerMapper;
    private final BuyerItemMapper buyerItemMapper;

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

    // 페이지네이션으로 바이어 전체조회 또는 사업유형으로 전채조회.
    @GetMapping
    public ResponseEntity getBuyers(@RequestParam @Positive int page,
                                    @RequestParam @Positive int size,
                                    @RequestParam(required = false) String businessType,
                                    Authentication authentication) {
        Page<Buyer> buyerPage = buyerService.findBuyers(page -1, size, businessType, authentication);
        List<Buyer> buyers = buyerPage.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(buyerMapper.buyerToBuyerResponseDtos(buyers), buyerPage), HttpStatus.OK);
    }

    @PatchMapping("/{buyer-id}")
    public ResponseEntity patchBuyer(@PathVariable("buyer-id") @Positive long buyerId,
                                     @RequestBody @Valid Dto.BuyerPatchDto patchDto,
                                     Authentication authentication) {
        patchDto.setBuyerId(buyerId);
        Buyer buyer = buyerService.updateBuyer(buyerMapper.buyerPatchDtoToBuyer(patchDto), authentication);
        return new ResponseEntity<>(
                new SingleResponseDto<>(buyerMapper.buyerToBuyerResponseDto(buyer)), HttpStatus.OK);
    }

    @DeleteMapping("/{buyer-id}")
    public ResponseEntity deleteBuyer(@PathVariable("buyer-id") @Positive long buyerId,
                                      Authentication authentication) {
        buyerService.deleteBuyer(buyerId, authentication);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}