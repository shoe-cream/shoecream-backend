package com.springboot.item.controller;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.dto.Dto;
import com.springboot.item.entity.Item;
import com.springboot.item.mapper.ItemMapper;
import com.springboot.item.service.ItemService;
import com.springboot.report.reportDto.ReportDto;
import com.springboot.report.service.SaleReport;
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
import java.util.Comparator;
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
    private final SaleReport saleReport;

    @PostMapping
    public ResponseEntity createItem(@Valid @RequestBody List<Dto.ItemPostDto> postDtos, Authentication authentication) {
        itemService.createItem(itemMapper.itemPostDtosToItems(postDtos), authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{itemCd}")
    public ResponseEntity getItem(@PathVariable("itemCd") String itemCd, Authentication authentication) {
        Item item = itemService.findItem(itemCd, authentication);
        ReportDto.InventoryDto report = saleReport.getInventory(itemCd);
        return new ResponseEntity<>(
                new SingleResponseDto<>(itemMapper.itemToResponseDto(item, report)), HttpStatus.OK);
    }
    @GetMapping("/all")
    public ResponseEntity getItemsAll() {
        List<Item> items = itemService.findItemsAll();

        List<Item> sortedItems = items.stream()
                .sorted(Comparator.comparing(Item::getItemNm))
                .collect(Collectors.toList());

        return new ResponseEntity(
                new SingleResponseDto<>(itemMapper.itemToResponseDtos(sortedItems)),HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity getItems(@RequestParam @Positive int page,
                                   @RequestParam @Positive int size,
                                   @RequestParam(required = false) String sort,
                                   @RequestParam(required = false) String direction,
                                   Authentication authentication) {
        String sortCriteria = "itemId";
        if(sort != null) {
            List<String> sorts = Arrays.asList("itemCd", "itemNm", "createdAt", "unitPrice", "itemId");
            if (sorts.contains(sort)) {
                sortCriteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<Item> itemPage = itemService.findItems(page-1, size, sortCriteria, direction, authentication);
        List<Dto.ItemResponseDto> itemResponseDtos =
                itemMapper.itemToResponseDtos(itemPage.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(itemResponseDtos, itemPage), HttpStatus.OK);
    }

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
    public ResponseEntity deleteItems(@RequestBody Dto.ItemDeleteRequestDto itemDeleteRequestDto, Authentication authentication) {
        List<Long> itemIds = itemDeleteRequestDto.getItemId();
        for(Long itemId : itemIds) {
            itemService.deleteItem(itemId, authentication);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
