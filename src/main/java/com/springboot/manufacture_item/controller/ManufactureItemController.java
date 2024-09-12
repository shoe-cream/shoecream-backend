package com.springboot.manufacture_item.controller;

import com.springboot.manufacture_item.dto.Dto;
import com.springboot.manufacture_item.entity.ItemManufacture;
import com.springboot.manufacture_item.mapper.ItemMfMapper;
import com.springboot.manufacture_item.service.ManufactureItemService;
import com.springboot.manufacture_history.entity.ManuFactureHistory;
import com.springboot.manufacture_history.mapper.ManufactureHistoryMapper;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import com.springboot.utils.UriCreator;
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
import java.net.URI;
import java.util.ArrayList;
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

    // 제조사 공급단가 전체 조회 mfCd/itemCd 선택사항
    @GetMapping
    public ResponseEntity getItemMfs(@RequestParam(required = false) String mfCd,
                                     @RequestParam(required = false) String itemCd,
                                     @Positive int page,
                                     @Positive int size,
                                     Authentication authentication) {
        Page<ItemManufacture> itemManufacturePage = manufactureItemService.findItemMfs(page - 1, size, itemCd, mfCd, authentication);
        List<ItemManufacture> itemManufactures = itemManufacturePage.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(
                        itemMfMapper.itemMfToItemMfResponseDtos(itemManufactures), itemManufacturePage), HttpStatus.OK);
    }

    @GetMapping("/{mfItemId}/histories")
    public ResponseEntity getHistory(@Positive @PathVariable("mfItemId") Long mfItemId,
                                     @Positive @RequestParam int page,
                                     @Positive @RequestParam int size,
                                     Authentication authentication) {
        Page<ManuFactureHistory> historyPages = manufactureItemService.findHistories(page - 1, size, mfItemId, authentication);
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
