package com.springboot.buyer_item.mapper;

import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.repository.BuyerRepository;
import com.springboot.buyer_item.dto.Dto;
import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BuyerItemMapper {
    BuyerItem buyerItemPostDtoToBuyerItem(Dto.BuyerItemPostDto postDto);

    BuyerItem buyerItemPatchDtoToBuyer(Dto.BuyerItemPatchDto patchDto);

    Dto.BuyerItemResponseDto buyerItemToBuyerResponseDto(BuyerItem buyerItem);

    List<Dto.BuyerItemResponseDto> buyerItemsToBuyerItemResponseDtos(List<BuyerItem> buyerItems) ;
}