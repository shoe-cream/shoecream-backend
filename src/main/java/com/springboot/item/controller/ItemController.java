package com.springboot.item.controller;


import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.dto.Dto;
import com.springboot.item.entity.Item;
import com.springboot.item.mapper.ItemMapper;
import com.springboot.item.service.ItemService;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemMapper itemMapper;
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity createItem(@Valid @RequestBody List<Dto.ItemPostDto> postDtos, Authentication authentication) {
        itemService.createItem(itemMapper.itemPostDtosToItems(postDtos), authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{itemCd}")
    public ResponseEntity getItem(@PathVariable("itemCd") String itemCd, Authentication authentication) {
        Item item = itemService.findItem(itemCd, authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(itemMapper.itemToResponseDto(item)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getItems(@RequestParam @Positive int page,
                                   @RequestParam @Positive int size,
                                   @RequestParam(required = false) String sort,
                                   Authentication authentication) {
        String sortCriteria = "createdAt";
        if(sort != null) {
            List<String> sorts = Arrays.asList("itemCd", "itemNm", "createdAt", "unitPrice");
            if (sorts.contains(sort)) {
                sortCriteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<Item> itemPage = itemService.findItems(page-1, size, sortCriteria, authentication);
        List<Dto.ItemResponseDto> itemResponseDtos =
                itemMapper.itemToResponseDtos(itemPage.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(itemResponseDtos, itemPage), HttpStatus.OK);
    }

//    @PatchMapping
//    public ResponseEntity updateItem(@Valid @RequestBody List<Dto.ItemPatchDto> patchDtos,
//                                     Authentication authentication) {
//        List<Item> patches = itemMapper.itemPatchDtosToItems(patchDtos);
//        List<Item> existingItems = itemService.findVerifiedItems(patchDtos.stream()
//                .map(Dto.ItemPatchDto::getItemId).collect(Collectors.toList()));
//
//        List<Item> updateList = new ArrayList<>();
//        for(int i = 0; i < patchDtos.size(); i++) {
//            Item item = itemService.updateItem(existingItems.get(i),patches.get(i), authentication);
//            updateList.add(item);
//        }
//
//        return new ResponseEntity<>(
//                new SingleResponseDto<>(itemMapper.itemToResponseDtos(updateList)), HttpStatus.OK);
//    }

    @PatchMapping
    public ResponseEntity updateItem(@Valid @RequestBody List<Dto.ItemPatchDto> patchDtos,
                                     Authentication authentication) {

        List<Item> response = new ArrayList<>();
        for (Dto.ItemPatchDto patchDto : patchDtos) {
            Item updateItem = itemService.updateItem(itemMapper.itemPatchToItem(patchDto), authentication);
            response.add(updateItem);
        }

        return new ResponseEntity<>(
                new SingleResponseDto<>(itemMapper.itemToResponseDtos(response)), HttpStatus.OK);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity deleteItem(@PathVariable("itemId") long itemId,
                                     Authentication authentication) {
        itemService.deleteItem(itemId, authentication);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity deleteItems(@RequestBody Dto.ItemDeleteRequestDto itemDeleteRequestDto) {
        List<Long> itemIds = itemDeleteRequestDto.getItemId();
        itemService.deleteItems(itemIds);  // 서비스에 아이템 ID 리스트 전달
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
