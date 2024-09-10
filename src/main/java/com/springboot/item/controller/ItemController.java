package com.springboot.item.controller;


import com.springboot.item.dto.Dto;
import com.springboot.item.entity.Item;
import com.springboot.item.mapper.ItemMapper;
import com.springboot.item.service.ItemService;
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
@RequiredArgsConstructor
@Transactional
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemMapper itemMapper;
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity createItem(@Valid @RequestBody Dto.ItemPostDto postDto, Authentication authentication) {
        itemService.createItem(itemMapper.itemPostDtoToItem(postDto), authentication);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{itemCd}")
    public ResponseEntity getItem(@PathVariable("itemCd") String itemCd, Authentication authentication) {
        Item item = itemService.findItem(itemCd, authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(itemMapper.itemToResponseDto(item)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getItems(@RequestParam(required = false) String itemNm,
                                   @RequestParam @Positive int page,
                                   @RequestParam @Positive int size,
                                   Authentication authentication) {
        Page<Item> itemPage = itemService.findItems(itemNm, page-1, size, authentication);
        List<Dto.ItemResponseDto> itemResponseDtos =
                itemMapper.itemToResponseDtos(itemPage.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(itemResponseDtos, itemPage), HttpStatus.OK);
    }

    @PatchMapping("/{itemCd}")
    public ResponseEntity updateItem(@PathVariable("itemCd") String itemCd,
                                     @RequestBody Dto.ItemPatchDto patchDto,
                                     Authentication authentication) {
        patchDto.setItemNm(itemCd);
        Item item = itemService.updateItem(itemMapper.itemPatchToItem(patchDto), authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(itemMapper.itemToResponseDto(item)), HttpStatus.OK);
    }

    @DeleteMapping("/{itemCd}")
    public ResponseEntity deleteItem(@PathVariable("itemCd") String itemCd,
                                     Authentication authentication) {
        itemService.deleteItem(itemCd, authentication);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
