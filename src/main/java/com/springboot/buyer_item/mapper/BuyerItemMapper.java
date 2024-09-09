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
        buyerItem.setUnitPrice(buyerItem.getUnitPrice());
        buyerItem.setStartDate(buyerItem.getStartDate());
        buyerItem.setEndDate(buyerItem.getEndDate());

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

        return responseDto;
    }

    List<Dto.BuyerItemResponseDto> buyerItemsToBuyerItemResponseDtos(List<BuyerItem> buyerItems) ;
}