package com.springboot.item_manufacture.controller;

import com.springboot.item_manufacture.dto.Dto;
import com.springboot.item_manufacture.entity.ItemManufacture;
import com.springboot.item_manufacture.mapper.ItemMfMapper;
import com.springboot.item_manufacture.service.ItemManufactureService;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/manufacture-items")
@Validated
@Transactional
public class ItemMfController {
    private final ItemMfMapper itemMfMapper;
    private final ItemManufactureService itemManufactureService;

    @PostMapping
    public ResponseEntity createItemMf(@Valid @RequestBody Dto.ItemMfPostDto postDto) {
        itemManufactureService.createItemMf(itemMfMapper.itemMfPostDtoToItemMf(postDto));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{mfItemId}")
    public ResponseEntity getItemMf(@PathVariable("mfItemId") @Positive long mfItemId) {
        ItemManufacture itemMf = itemManufactureService.findItemMf(mfItemId);

        return new ResponseEntity<>(
                new SingleResponseDto<>(itemMfMapper.itemMfToItemMfResponseDto(itemMf)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getItemMfs(@RequestParam(required = false) String mfCd,
                                     @RequestParam(required = false) String itemCd,
                                     @Positive int page,
                                     @Positive int size) {
        Page<ItemManufacture> itemManufacturePage = itemManufactureService.findItemMfs(page - 1, size, itemCd, mfCd);
        List<ItemManufacture> itemManufactures = itemManufacturePage.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(
                        itemMfMapper.itemMfToItemMfResponseDtos(itemManufactures), itemManufacturePage), HttpStatus.OK);
    }
}
