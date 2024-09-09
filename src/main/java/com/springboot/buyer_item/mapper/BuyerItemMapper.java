package com.springboot.buyer_item.mapper;

import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.repository.BuyerRepository;
import com.springboot.buyer_item.dto.Dto;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.item.entity.Item;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BuyerItemMapper {
    default BuyerItem buyerItemPostDtoToBuyerItem(Dto.BuyerItemPostDto postDto){
        Buyer buyer = new Buyer();
        buyer.setBuyerId(postDto.getBuyerId());
        Item item = new Item();
        item.setItemId(postDto.getItemId());

        BuyerItem buyerItem = new BuyerItem();
        buyerItem.setBuyer(buyer);
        buyerItem.setItem(item);
        buyerItem.setUnitPrice(postDto.getUnitPrice());
        buyerItem.setStartDate(postDto.getStartDate());
        buyerItem.setEndDate(postDto.getEndDate());

        return buyerItem;
    }

    BuyerItem buyerItemPatchDtoToBuyer(Dto.BuyerItemPatchDto patchDto);

    default Dto.BuyerItemResponseDto buyerItemToBuyerResponseDto(BuyerItem buyerItem) {
        Dto.BuyerItemResponseDto responseDto = new Dto.BuyerItemResponseDto();

        responseDto.setBuyerNm(buyerItem.getBuyer().getBuyerNm());
        responseDto.setItemNm(buyerItem.getItem().getItemNm());
        responseDto.setItemCd(buyerItem.getItem().getItemCd());
        responseDto.setItemStatus(buyerItem.getItem().getItemStatus());
        responseDto.setUnit(buyerItem.getItem().getUnit());
        responseDto.setUnitPrice(buyerItem.getUnitPrice());
        responseDto.setStartDate(buyerItem.getStartDate());
        responseDto.setEndDate(buyerItem.getEndDate());

        return responseDto;
    }

    // 수정 필요
    default List<Dto.BuyerItemResponseDto> buyerItemsToBuyerItemResponseDtos(List<BuyerItem> buyerItems) {
        return buyerItems
                .stream()
                .map(buyerItem -> Dto.BuyerItemResponseDto
                        .builder()
                        .itemNm(buyerItem.getItem().getItemNm())
                        .itemCd(buyerItem.getItem().getItemCd())
                        .buyerNm(buyerItem.getBuyer().getBuyerNm())
                        .unit(buyerItem.getItem().getUnit())
                        .unitPrice(buyerItem.getUnitPrice())
                        .startDate(buyerItem.getStartDate())
                        .endDate(buyerItem.getEndDate())
                        .itemStatus(buyerItem.getItem().getItemStatus())
                        .build())
                .collect(Collectors.toList());
    }
}