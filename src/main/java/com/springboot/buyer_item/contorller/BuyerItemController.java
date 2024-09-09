package com.springboot.buyer_item.contorller;

import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer_item.dto.Dto;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.buyer_item.mapper.BuyerItemMapper;
import com.springboot.buyer_item.service.BuyerItemService;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/buyer-item")
@RequiredArgsConstructor
@Valid
@Transactional
public class BuyerItemController {
    private final BuyerItemService buyerItemService;
    private final BuyerItemMapper buyerItemMapper;

    @PostMapping
    public ResponseEntity postBuyerItem(@RequestBody @Valid Dto.BuyerItemPostDto postDto) {
        buyerItemService.createBuyerItem(buyerItemMapper.buyerItemPostDtoToBuyerItem(postDto));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 특정 바이어아이템 조회
    @GetMapping("/{buyerItemId}")
    public ResponseEntity findBuyerItem(@PathVariable("buyerItemId") @Positive long buyerItemId) {
        BuyerItem buyerItem = buyerItemService.findBuyerItem(buyerItemId);

        return new ResponseEntity<>(
                new SingleResponseDto<>(buyerItemMapper.buyerItemToBuyerResponseDto(buyerItem)), HttpStatus.OK);
    }

    // 페이지네이션으로 바이어아이템 전체조회 또는 특정 조건을 사용자가 입력해 전체 조회
    @GetMapping
    public ResponseEntity getBuyerItems(@RequestParam(required = false) String buyerCd,
                                        @RequestParam(required = false) String buyerNm,
                                        @RequestParam(required = false) String itemNm,
                                        @RequestParam(required = false) String itemCd,
                                        @RequestParam @Positive int page,
                                        @RequestParam @Positive int size) {

        // 바이어 코드, 이름, 아이템 이름, 아이템 코드, 날짜 범위로 조회
        Page<BuyerItem> buyerItemPage = buyerItemService.findBuyerItems(page - 1, size, buyerCd, buyerNm, itemNm, itemCd);

        List<Dto.BuyerItemResponseDto> buyerItemResponseDtos =
                buyerItemMapper.buyerItemsToBuyerItemResponseDtos(buyerItemPage.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(buyerItemResponseDtos, buyerItemPage), HttpStatus.OK);
    }

    @PatchMapping("/{buyerItem-id}")
    public ResponseEntity updateBuyerItem(@PathVariable @Positive long buyerItemId,
                                          @RequestBody Dto.BuyerItemPatchDto patchDto) {
        patchDto.setBuyerItemId(buyerItemId);
        BuyerItem buyerItem = buyerItemMapper.buyerItemPatchDtoToBuyer(patchDto);

        return new ResponseEntity<>(
                new SingleResponseDto<>(buyerItemMapper.buyerItemToBuyerResponseDto(buyerItem)), HttpStatus.OK);

    }

    @DeleteMapping("/{buyer-id}")
    public ResponseEntity deleteBuyerItem(@PathVariable @Positive long buyerItemId) {
        buyerItemService.deleteBuyerItem(buyerItemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

