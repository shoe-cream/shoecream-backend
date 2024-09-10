package com.springboot.item_manufacture.controller;

import com.springboot.item_manufacture.dto.Dto;
import com.springboot.item_manufacture.entity.ItemManufacture;
import com.springboot.item_manufacture.mapper.ItemMfMapper;
import com.springboot.item_manufacture.service.ItemManufactureService;
import com.springboot.manufacture_history.entity.ManuFactureHistory;
import com.springboot.manufacture_history.mapper.ManufactureHistoryMapper;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import com.springboot.sale_history.entity.SaleHistory;
import com.springboot.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manufacture-items")
@Validated
@Transactional
public class ItemMfController {
    private final ItemMfMapper itemMfMapper;
    private final ItemManufactureService itemManufactureService;
    private final static String MANUFACTURE_ITEM_DEFAULT_URL = "/manufacture-items";
    private final ManufactureHistoryMapper manufactureHistoryMapper;

    @PostMapping
    public ResponseEntity createItemMf(@Valid @RequestBody Dto.ItemMfPostDto postDto, Authentication authentication) {
        ItemManufacture itemManufacture = itemManufactureService.createItemMf(itemMfMapper.itemMfPostDtoToItemMf(postDto), authentication);

        URI location = UriCreator.createUri(MANUFACTURE_ITEM_DEFAULT_URL,itemManufacture.getMfItemId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{mfItemId}")
    public ResponseEntity getItemMf(@PathVariable("mfItemId") @Positive long mfItemId, Authentication authentication) {
        ItemManufacture itemMf = itemManufactureService.findItemMf(mfItemId, authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(itemMfMapper.itemMfToItemMfResponseDto(itemMf)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getItemMfs(@RequestParam(required = false) String mfCd,
                                     @RequestParam(required = false) String itemCd,
                                     @Positive int page,
                                     @Positive int size,
                                     Authentication authentication) {
        Page<ItemManufacture> itemManufacturePage = itemManufactureService.findItemMfs(page - 1, size, itemCd, mfCd, authentication);
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
        Page<ManuFactureHistory> historyPages = itemManufactureService.findHistories(page - 1, size, mfItemId, authentication);
        List<ManuFactureHistory> historyLists = historyPages.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(
                        manufactureHistoryMapper.mfHistoriesToMfHistoriesResponseDtos(historyLists),historyPages), HttpStatus.OK);
    }

}
