package com.springboot.buyer.controller;

import com.springboot.buyer.dto.Dto;
import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.mapper.BuyerMapper;
import com.springboot.buyer.service.BuyerService;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/buyer")
@RequiredArgsConstructor
@Validated
@Transactional
public class BuyerController {
    private final BuyerService buyerService;
    private final BuyerMapper buyerMapper;

    @PostMapping
    public ResponseEntity postBuyer(@Valid @RequestBody Dto.BuyerPostDto postDto) {
        buyerService.createBuyer(buyerMapper.buyerPostDtoToBuyer(postDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity getBuyerByCd(@RequestParam(required = false) String buyerCd,
                                       @RequestParam(required = false) String buyerNm,
                                       @RequestParam(required = false) String businessType) {
        Buyer buyerByCd = buyerService.findBuyerByFilter(buyerCd, buyerNm, businessType);
        return new ResponseEntity<>(
                new SingleResponseDto<>(buyerMapper.buyerToBuyerResponseDto(buyerByCd)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getBuyers(@RequestParam @Positive int page,
                                    @RequestParam @Positive int size,
                                    @RequestParam(required = false) String businessType) {
        Page<Buyer> buyerPage = buyerService.findBuyers(page -1, size, businessType);
        List<Buyer> buyers = buyerPage.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(buyerMapper.buyerToBuyerResponseDtos(buyers), buyerPage), HttpStatus.OK);
    }

    @PatchMapping("/{buyer-id}")
    public ResponseEntity patchBuyer(@PathVariable("buyer-id") @Positive long buyerId,
                                     @RequestBody @Valid Dto.BuyerPatchDto patchDto) {
        patchDto.setBuyerId(buyerId);
        Buyer buyer = buyerService.updateBuyer(buyerMapper.buyerPatchDtoToBuyer(patchDto));
        return new ResponseEntity<>(
                new SingleResponseDto<>(buyerMapper.buyerToBuyerResponseDto(buyer)), HttpStatus.OK);
    }

    @DeleteMapping("/{buyer-id}")
    public ResponseEntity deleteBuyer(@PathVariable("buyer-id") @Positive long buyerId) {
        buyerService.deleteBuyer(buyerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}