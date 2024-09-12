package com.springboot.buyer_item.contorller;

import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer_item.dto.Dto;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.buyer_item.mapper.BuyerItemMapper;
import com.springboot.buyer_item.service.BuyerItemService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/buyer-items")
@RequiredArgsConstructor
@Valid
@Transactional
public class BuyerItemController {
    private final BuyerItemService buyerItemService;
    private final BuyerItemMapper buyerItemMapper;

    @PostMapping
    public ResponseEntity postBuyerItem(@RequestBody @Valid List<Dto.BuyerItemPostDto> postDto, Authentication authentication) {

        buyerItemService.createBuyerItem(buyerItemMapper.postDtosToBuyerItems(postDto), authentication);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 특정 바이어아이템 조회
    @GetMapping("/{buyerItemId}")
    public ResponseEntity findBuyerItem(@PathVariable("buyerItemId") @Positive long buyerItemId, Authentication authentication) {
        BuyerItem buyerItem = buyerItemService.findBuyerItem(buyerItemId, authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(buyerItemMapper.buyerItemToBuyerResponseDto(buyerItem)), HttpStatus.OK);
    }

    // 수정 필요!!
    // 페이지네이션으로 바이어아이템 전체조회 또는 특정 조건을 사용자가 입력해 전체 조회
    @GetMapping
    public ResponseEntity getBuyerItems(@RequestParam(required = false) String buyerCd,
                                        @RequestParam @Positive int page,
                                        @RequestParam @Positive int size,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(required = false) String direction,
                                        Authentication authentication) {

        String criteria = "buyerItemId";
        if(sort != null) {
            List<String> sorts = Arrays.asList("buyerItemId", "buyerCd", "unitPrice", "startDate", "endDate", "modifiedAt");
            if (sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<BuyerItem> buyerItemPage = buyerItemService.findBuyerItems(page - 1, size, buyerCd, criteria, direction, authentication);

        List<Dto.BuyerItemResponseDto> buyerItemResponseDtos =
                buyerItemMapper.buyerItemsToBuyerItemResponseDtos(buyerItemPage.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(buyerItemResponseDtos, buyerItemPage), HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity updateBuyerItem(@RequestBody List<Dto.BuyerItemPatchDto> patchDtos,
                                          Authentication authentication) {

        List<BuyerItem> buyerItems = new ArrayList<>();
        for(Dto.BuyerItemPatchDto patchDto : patchDtos) {
            BuyerItem buyerItem =
                    buyerItemService.updateBuyerItem(buyerItemMapper.buyerItemPatchDtoToBuyer(patchDto), authentication);
            buyerItems.add(buyerItem);
        }

        return new ResponseEntity<>(
                new SingleResponseDto<>(buyerItemMapper.buyerItemsToBuyerItemResponseDtos(buyerItems)), HttpStatus.OK);

    }

    @DeleteMapping("/{buyerItemId}")
    public ResponseEntity deleteBuyerItem(@PathVariable("buyerItemId") @Positive long buyerItemId,
                                          Authentication authentication) {
        buyerItemService.deleteBuyerItem(buyerItemId, authentication);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

