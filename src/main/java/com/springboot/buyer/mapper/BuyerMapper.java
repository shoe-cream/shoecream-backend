package com.springboot.buyer.mapper;

import com.springboot.buyer.dto.Dto;
import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.buyer_item.dto.Dto.BuyerItemResponseDto;

import com.springboot.item.entity.Item;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BuyerMapper {
    Buyer buyerPostDtoToBuyer(Dto.BuyerPostDto postDto);
    Buyer buyerPatchDtoToBuyer(Dto.BuyerPatchDto patchDto);
    Dto.BuyerResponse buyerToBuyerResponseDto(Buyer buyer);
    List<Dto.BuyerResponse> buyerToBuyerResponseDtos(List<Buyer> buyers);

    default Dto.BuyerResponseWithItemDto buyerItemsToBuyerResponseWithItems(Buyer buyer) {
        Dto.BuyerResponseWithItemDto response = new Dto.BuyerResponseWithItemDto();

        response.setBuyerCd(buyer.getBuyerCd());
        response.setBuyerNm(buyer.getBuyerNm());
        response.setTel(buyer.getTel());

        List<BuyerItemResponseDto> itemResponseDtos = buyer.getBuyerItems()
                .stream()
                .filter(item -> item.getItem().getItemStatus() != Item.ItemStatus.NOT_FOR_SALE)
                .map(item -> {
                    BuyerItemResponseDto itemDto = new BuyerItemResponseDto();
                    itemDto.setBuyerNm(item.getBuyer().getBuyerNm());
                    itemDto.setItemCd(item.getItem().getItemCd());
                    itemDto.setItemNm(item.getItem().getItemNm());
                    itemDto.setUnit(item.getItem().getUnit());
                    itemDto.setStartDate(item.getStartDate());
                    itemDto.setEndDate(item.getEndDate());
                    itemDto.setItemStatus(item.getItem().getItemStatus());
                    return itemDto;
                })
                .collect(Collectors.toList());

        response.setBuyerItems(itemResponseDtos);

        return response;
    }
}
