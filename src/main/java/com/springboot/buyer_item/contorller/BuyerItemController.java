package com.springboot.buyer_item.contorller;

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
    @GetMapping("/{buyerItemCd}")
    public ResponseEntity findBuyerItem(@PathVariable("buyerItemCd") String buyerItemCd, Authentication authentication) {
        List<BuyerItem> buyerItems = buyerItemService.findBuyerItem(buyerItemCd, authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(buyerItemMapper.buyerItemsToBuyerItemResponseDtos(buyerItems)), HttpStatus.OK);
    }

    //전체조회 (Pagination, buyerCd / buyerNm / itemCd / itemNm 등 유동적으로 검색 )
    @GetMapping
    public ResponseEntity getBuyerItems(@RequestParam(required = false) String buyerCd,
                                        @RequestParam(required = false) String buyerNm,
                                        @RequestParam(required = false) String itemCd,
                                        @RequestParam(required = false) String itemNm,
                                        @RequestParam @Positive int page,
                                        @RequestParam @Positive int size,
                                        @RequestParam(required = false) String sort,
                                        @RequestParam(required = false) String direction,
                                        Authentication authentication) {

        String criteria = "buyerItemId";
        if(sort != null) {
            List<String> sorts = Arrays.asList("buyerItemId", "buyer.buyerCd", "unitPrice", "startDate", "endDate", "modifiedAt");
            if (sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<BuyerItem> buyerItemPage = buyerItemService.findBuyerItems(page - 1, size, buyerCd, buyerNm, itemCd, itemNm, criteria, direction, authentication);

        List<Dto.BuyerItemResponseDto> buyerItemResponseDtos =
                buyerItemMapper.buyerItemsToBuyerItemResponseDtos(buyerItemPage.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(buyerItemResponseDtos, buyerItemPage), HttpStatus.OK);
    }

    @GetMapping("/period")
    public ResponseEntity getBuyerByBuyerCdBetweenPeriod (@RequestParam(required = false) String buyerCd,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                          @RequestParam @Positive int page,
                                                          @RequestParam @Positive int size,
                                                          @RequestParam(required = false) String sort,
                                                          @RequestParam(required = false) String direction,
                                                          Authentication authentication) {

        String criteria = "buyerItemId";
        if(sort != null) {
            List<String> sorts = Arrays.asList("buyerItemId", "buyer.buyerCd", "unitPrice", "startDate", "endDate", "modifiedAt");
            if (sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<BuyerItem> buyerItemPage = buyerItemService.findBuyerItemsByBuyerCdAndCurrentDate(buyerCd, date, page - 1, size, criteria, direction);

        List<Dto.BuyerItemResponseDto> buyerItemResponseDtos =
                buyerItemMapper.buyerItemsToBuyerItemResponseDtos(buyerItemPage.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(buyerItemResponseDtos, buyerItemPage), HttpStatus.OK);
    }

    //BuyerItem 수정
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

    //BuyerItem 삭제 - buyerId를 통해
    @DeleteMapping
    public ResponseEntity deleteBuyerItems(@RequestBody Dto.BuyerItemDeleteDtos deleteDtos, Authentication authentication) {
        List<Long> deleteIds = deleteDtos.getItemId();
        for(Long id : deleteIds) {
            buyerItemService.deleteBuyerItem(id, authentication);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

