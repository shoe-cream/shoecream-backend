package com.springboot.manufacture_item.controller;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.manufacture_item.dto.Dto;
import com.springboot.manufacture_item.entity.ItemManufacture;
import com.springboot.manufacture_item.mapper.ItemMfMapper;
import com.springboot.manufacture_item.service.ManufactureItemService;
import com.springboot.manufacture_history.entity.ManuFactureHistory;
import com.springboot.manufacture_history.mapper.ManufactureHistoryMapper;
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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manufacture-items")
@Validated
@Transactional
public class ManufactureItemController {
    private final ItemMfMapper itemMfMapper;
    private final ManufactureItemService manufactureItemService;
    private final ManufactureHistoryMapper manufactureHistoryMapper;

    // 공급사 등록(공급 단가)
    @PostMapping
    public ResponseEntity createItemMf(@Valid @RequestBody List<Dto.ItemMfPostDto> postDtos, Authentication authentication) {
        manufactureItemService.createItemMf(itemMfMapper.postDtosToItemManufactures(postDtos), authentication);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    // 제조사 공급단가 조회
    @GetMapping("/{mfItemId}")
    public ResponseEntity getItemMf(@PathVariable("mfItemId") @Positive long mfItemId, Authentication authentication) {
        ItemManufacture itemMf = manufactureItemService.findItemMf(mfItemId, authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(itemMfMapper.itemMfToItemMfResponseDto(itemMf)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getItemMfs(@RequestParam(required = false) String mfCd,
                                     @RequestParam(required = false) String mfNm,
                                     @RequestParam(required = false) String itemCd,
                                     @RequestParam(required = false) String itemNm,
                                     @RequestParam(required = false) String region,
                                     @Positive int page,
                                     @Positive int size,
                                     @RequestParam(required = false) String sort,
                                     @RequestParam(required = false) String direction,
                                     Authentication authentication) {

        String criteria = "mfItemId";
        if(sort != null) {
            List<String> sorts = Arrays.asList("mfItemId", "unitPrice", "createdAt", "modifiedAt", "qty", "region", "itemNm", "itemCd", "mfNm");
            if (sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<ItemManufacture> itemManufacturePage = manufactureItemService.findItemMfs(page - 1, size, criteria, direction, itemNm, itemCd, mfNm, mfCd, region);
        List<ItemManufacture> itemManufactures = itemManufacturePage.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(
                        itemMfMapper.itemMfToItemMfResponseDtos(itemManufactures), itemManufacturePage), HttpStatus.OK);
    }

    @GetMapping("/{mfItemId}/histories")
    public ResponseEntity getHistory(@Positive @PathVariable("mfItemId") Long mfItemId,
                                     @Positive @RequestParam int page,
                                     @Positive @RequestParam int size,
                                     @RequestParam(required = false) String sort,
                                     @RequestParam(required = false) String direction,
                                     Authentication authentication) {

        String criteria = "createdAt";
        if(sort != null) {
            List<String> sorts = Arrays.asList("mfItemId", "mfCd", "createdAt", "receiveDate", "qty", "unitPrice", "author", "mfHistoryId");
            if (sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<ManuFactureHistory> historyPages = manufactureItemService.findHistories(page - 1, size, sort, direction, mfItemId, authentication);
        List<ManuFactureHistory> historyLists = historyPages.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(
                        manufactureHistoryMapper.mfHistoriesToMfHistoriesResponseDtos(historyLists),historyPages), HttpStatus.OK);
    }

    // 제조사 공급단가, 수량 수정
    @PatchMapping
    public ResponseEntity updateItemMf(@RequestBody @Valid List<Dto.ItemMfPatchDto> patchDtos,
                                       Authentication authentication) {

        List<ItemManufacture> itemManufactures = new ArrayList<>();
        for(Dto.ItemMfPatchDto patchDto : patchDtos) {

          ItemManufacture itemManufacture = manufactureItemService.updateItemMf(itemMfMapper.itemMfPatchDtoToItemMf(patchDto), authentication);
            itemManufactures.add(itemManufacture);
        }
        return new ResponseEntity(
                new SingleResponseDto<>(itemMfMapper.itemMfToItemMfResponseDtos(itemManufactures)), HttpStatus.OK);
    }
}
